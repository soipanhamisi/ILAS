package org.soipan.ilas.services;

import org.soipan.ilas.dto.QuestionGradeRequest;
import org.soipan.ilas.dto.ExamQuestionDTO;
import org.soipan.ilas.dto.InstructorDashboardSummaryDTO;
import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for Instructor-related exam operations
 */
@Service
public class InstructorExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSubmissionRepository submissionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Instructor Use Case 1: Create Assessment
     * Instructor creates an exam with CSV template for a course
     */
    @Transactional
    public Exam createAssessment(int instructorId, int courseId, String examTitle,
                                  int maxScore, MultipartFile csvFile) {
        // Verify instructor exists
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Verify course exists and belongs to instructor
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own this course");
        }

        // Check if exam with same title already exists for this course
        if (examRepository.existsByCourse_CourseIdAndExamTitle(courseId, examTitle)) {
            throw new IllegalArgumentException("Exam with title '" + examTitle + "' already exists for this course");
        }

        // Store the CSV file
        String csvFilePath = fileStorageService.storeFile(csvFile, "exam_" + courseId);

        // Create and save exam
        Exam exam = new Exam(course, examTitle, maxScore, csvFilePath);
        return examRepository.save(exam);
    }

    /**
     * Instructor Use Case 2: Issue Exam Grade
     * Instructor grades a student's exam submission
     */
    @Transactional
    public ExamSubmission issueGrade(int instructorId, Long submissionId,
                                     List<QuestionGradeRequest> questionGrades) {
        // Verify instructor exists
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Find submission
        ExamSubmission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with ID: " + submissionId));

        // Verify instructor owns the course
        Course course = submission.getExam().getCourse();
        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        List<String> examQuestions = getExamQuestions(instructorId, submission.getExam().getExamId());
        if (questionGrades.size() != examQuestions.size()) {
            throw new IllegalArgumentException("All questions must be graded before submission");
        }

        Map<Integer, QuestionGradeRequest> gradeByQuestionNumber = questionGrades.stream()
                .collect(Collectors.toMap(
                        QuestionGradeRequest::getQuestionNumber,
                        Function.identity(),
                        (left, right) -> right
                ));

        for (int i = 1; i <= examQuestions.size(); i++) {
            if (!gradeByQuestionNumber.containsKey(i)) {
                throw new IllegalArgumentException("Missing grade for question " + i);
            }
        }

        int totalGrade = 0;
        StringBuilder feedbackBuilder = new StringBuilder();
        StringBuilder justificationBuilder = new StringBuilder();

        for (int i = 1; i <= examQuestions.size(); i++) {
            QuestionGradeRequest questionGrade = gradeByQuestionNumber.get(i);

            if (questionGrade.getGrade() == null || questionGrade.getGrade() < 0) {
                throw new IllegalArgumentException("Grade for question " + i + " is invalid");
            }

            if (questionGrade.getFeedback() == null || questionGrade.getFeedback().trim().isEmpty()) {
                throw new IllegalArgumentException("Feedback for question " + i + " is required");
            }

            if (questionGrade.getGradeJustification() == null
                    || questionGrade.getGradeJustification().trim().isEmpty()) {
                throw new IllegalArgumentException("Justification for question " + i + " is required");
            }

            totalGrade += questionGrade.getGrade();

            if (feedbackBuilder.length() > 0) {
                feedbackBuilder.append("\n\n");
                justificationBuilder.append("\n\n");
            }

            feedbackBuilder
                    .append("Q")
                    .append(i)
                    .append(" Feedback: ")
                    .append(questionGrade.getFeedback().trim());

            justificationBuilder
                    .append("Q")
                    .append(i)
                    .append(" Justification: ")
                    .append(questionGrade.getGradeJustification().trim());
        }

        // Validate grade
        int maxScore = submission.getExam().getMaxScore();
        if (totalGrade > maxScore) {
            throw new IllegalArgumentException("Total grade cannot exceed " + maxScore);
        }

        // Set aggregate grade and per-question feedback summaries
        submission.setGrade(totalGrade);
        submission.setFeedback(feedbackBuilder.toString());
        submission.setGradeJustification(justificationBuilder.toString());
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(instructor);

        return submissionRepository.save(submission);
    }

    /**
     * Backward-compatible aggregate grading entrypoint.
     */
    @Transactional
    public ExamSubmission issueGrade(int instructorId, Long submissionId, int grade,
                                     String feedback, String gradeJustification) {
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        ExamSubmission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with ID: " + submissionId));

        Course course = submission.getExam().getCourse();
        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        int maxScore = submission.getExam().getMaxScore();
        if (grade < 0 || grade > maxScore) {
            throw new IllegalArgumentException("Grade must be between 0 and " + maxScore);
        }

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        submission.setGradeJustification(gradeJustification);
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(instructor);
        return submissionRepository.save(submission);
    }

    /**
     * Instructor Use Case 3: Issue Comments and Grade Justifications
     * Update feedback and justification for an already graded submission
     */
    @Transactional
    public ExamSubmission updateFeedback(int instructorId, Long submissionId,
                                          String feedback, String gradeJustification) {
        // Verify instructor exists
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Find submission
        ExamSubmission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with ID: " + submissionId));

        // Verify instructor owns the course
        Course course = submission.getExam().getCourse();
        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        // Update feedback
        submission.setFeedback(feedback);
        submission.setGradeJustification(gradeJustification);
        submission.setGradedBy(instructor);

        return submissionRepository.save(submission);
    }

    /**
     * Get all submissions for an exam (for grading purposes)
     */
    public List<ExamSubmission> getSubmissionsForExam(int instructorId, Long examId) {
        // Verify instructor exists
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Verify exam exists
        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        // Verify instructor owns the course
        if (exam.getCourse().getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        return submissionRepository.findByExam_ExamId(examId);
    }

    /**
     * Get ungraded submissions for an exam
     */
    public List<ExamSubmission> getUngradedSubmissions(int instructorId, Long examId) {
        // Verify instructor exists
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Verify exam exists
        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        // Verify instructor owns the course
        if (exam.getCourse().getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        return submissionRepository.findByExam_ExamIdAndGradeIsNull(examId);
    }

    /**
     * Get all exams for instructor's course
     */
    public List<Exam> getExamsForCourse(int instructorId, int courseId) {
        // Verify instructor exists
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Verify course exists and belongs to instructor
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own this course");
        }

        return examRepository.findByCourse_CourseId(courseId);
    }

    /**
     * Get instructor dashboard aggregate metrics.
     */
    public InstructorDashboardSummaryDTO getDashboardSummary(int instructorId) {
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        List<Course> courses = courseRepository.findByInstructor_InstructorId(instructorId);
        LocalDateTime recentCutoff = LocalDateTime.now().minusDays(7);
        LocalDate trendEndDate = LocalDate.now();
        int trendWindowDays = 14;

        int totalNewEnrollments = 0;
        int totalTestsToBeGraded = 0;
        double totalEarnedScore = 0;
        double totalPossibleScore = 0;

        List<InstructorDashboardSummaryDTO.CourseSummaryDTO> courseSummaries = new ArrayList<>();
        List<InstructorDashboardSummaryDTO.GradingQueueItemDTO> gradingQueueItems = new ArrayList<>();
        Map<LocalDate, Set<Integer>> overallActiveStudentsByDay = createDailyStudentBucket(trendEndDate, trendWindowDays);

        for (Course course : courses) {
            int courseId = course.getCourseId();
            List<Enrollment> activeEnrollments = enrollmentRepository
                    .findByCourse_CourseIdAndEnrollmentStatus(courseId, EnrollmentStatus.ACTIVE);
            Set<Integer> activeEnrollmentStudentIds = activeEnrollments.stream()
                    .map(enrollment -> enrollment.getStudent().getStudentId())
                    .collect(Collectors.toSet());
            Map<LocalDate, Set<Integer>> courseActiveStudentsByDay = createDailyStudentBucket(trendEndDate, trendWindowDays);

            int courseNewEnrollments = (int) activeEnrollments.stream()
                    .filter(enrollment -> enrollment.getEnrolledAt() != null
                            && !enrollment.getEnrolledAt().isBefore(recentCutoff))
                    .count();

            List<Exam> courseExams = examRepository.findByCourse_CourseId(courseId);
            int courseTestsToBeGraded = 0;
            double courseEarnedScore = 0;
            double coursePossibleScore = 0;

            for (Exam exam : courseExams) {
                long examUngradedCount = submissionRepository.countByExam_ExamIdAndGradeIsNull(exam.getExamId());
                if (examUngradedCount > 0) {
                    courseTestsToBeGraded += (int) examUngradedCount;
                    gradingQueueItems.add(new InstructorDashboardSummaryDTO.GradingQueueItemDTO(
                            exam.getExamId(),
                            exam.getExamTitle(),
                            courseId,
                            course.getCourseTitle(),
                            (int) examUngradedCount
                    ));
                }

                List<ExamSubmission> submissions = submissionRepository.findByExam_ExamId(exam.getExamId());
                for (ExamSubmission submission : submissions) {
                    LocalDateTime submittedAt = submission.getSubmittedAt();
                    int studentId = submission.getStudent().getStudentId();
                    if (submittedAt != null && activeEnrollmentStudentIds.contains(studentId)) {
                        LocalDate submissionDate = submittedAt.toLocalDate();
                        if (courseActiveStudentsByDay.containsKey(submissionDate)) {
                            courseActiveStudentsByDay.get(submissionDate).add(studentId);
                            overallActiveStudentsByDay.get(submissionDate).add(studentId);
                        }
                    }

                    if (submission.getGrade() == null) {
                        continue;
                    }

                    courseEarnedScore += submission.getGrade();
                    coursePossibleScore += exam.getMaxScore();
                }
            }

            double courseAveragePerformance = coursePossibleScore > 0
                    ? (courseEarnedScore / coursePossibleScore) * 100
                    : 0;

            totalNewEnrollments += courseNewEnrollments;
            totalTestsToBeGraded += courseTestsToBeGraded;
            totalEarnedScore += courseEarnedScore;
            totalPossibleScore += coursePossibleScore;

            courseSummaries.add(new InstructorDashboardSummaryDTO.CourseSummaryDTO(
                    courseId,
                    course.getCourseTitle(),
                    activeEnrollments.size(),
                    courseNewEnrollments,
                    courseAveragePerformance,
                    courseTestsToBeGraded,
                    toTrendPoints(courseActiveStudentsByDay)
            ));
        }

        double averagePerformance = totalPossibleScore > 0
                ? (totalEarnedScore / totalPossibleScore) * 100
                : 0;

        return new InstructorDashboardSummaryDTO(
                courses.size(),
                totalNewEnrollments,
                averagePerformance,
                totalTestsToBeGraded,
                toTrendPoints(overallActiveStudentsByDay),
                courseSummaries,
                gradingQueueItems
        );
    }

    private Map<LocalDate, Set<Integer>> createDailyStudentBucket(LocalDate endDate, int windowDays) {
        Map<LocalDate, Set<Integer>> buckets = new LinkedHashMap<>();
        LocalDate startDate = endDate.minusDays(Math.max(windowDays - 1, 0));
        for (int i = 0; i < windowDays; i++) {
            buckets.put(startDate.plusDays(i), new HashSet<>());
        }
        return buckets;
    }

    private List<InstructorDashboardSummaryDTO.DailyActiveStudentsPointDTO> toTrendPoints(
            Map<LocalDate, Set<Integer>> studentsByDay) {
        if (studentsByDay == null || studentsByDay.isEmpty()) {
            return new ArrayList<>();
        }

        List<InstructorDashboardSummaryDTO.DailyActiveStudentsPointDTO> points = new ArrayList<>();
        studentsByDay.forEach((date, studentIds) -> points.add(
                new InstructorDashboardSummaryDTO.DailyActiveStudentsPointDTO(
                        date.toString(),
                        studentIds == null ? 0 : studentIds.size()
                )));
        return points;
    }

    /**
     * Get exam questions from the exam CSV.
     */
    public List<String> getExamQuestions(int instructorId, Long examId) {
        return getExamQuestionDetails(instructorId, examId).stream()
                .map(ExamQuestionDTO::getQuestionText)
                .toList();
    }

    /**
     * Get exam questions including per-question max grade from CSV.
     */
    public List<ExamQuestionDTO> getExamQuestionDetails(int instructorId, Long examId) {
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        if (exam.getCourse().getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        String csvPath = exam.getCsvFilePath();
        if (csvPath == null || csvPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam questions are not available");
        }

        try {
            Path path = Paths.get(csvPath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Exam question file was not found");
            }

            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .map(this::extractQuestionDetails)
                    .filter(question -> !question.getQuestionText().isEmpty())
                    .collect(Collectors.collectingAndThen(Collectors.toList(), questions -> {
                        for (int i = 0; i < questions.size(); i++) {
                            questions.get(i).setQuestionNumber(i + 1);
                        }
                        return questions;
                    }));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read exam questions", ex);
        }
    }

    private ExamQuestionDTO extractQuestionDetails(String line) {
        List<String> columns = splitCsvColumns(line);
        String questionText = columns.isEmpty() ? "" : columns.get(0).trim();
        String maxGradeRaw = columns.size() > 2 ? columns.get(2).trim() : "";

        if (questionText.toLowerCase(Locale.ROOT).startsWith("question")) {
            return new ExamQuestionDTO(null, "", 0);
        }

        return new ExamQuestionDTO(null, questionText, parseMaxGrade(maxGradeRaw));
    }

    private int parseMaxGrade(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }

        String digitsOnly = raw.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(digitsOnly);
    }

    private List<String> splitCsvColumns(String line) {
        List<String> columns = new java.util.ArrayList<>();
        if (line == null) {
            return columns;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (ch == ',' && !inQuotes) {
                columns.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(ch);
        }

        columns.add(current.toString());
        return columns;
    }

}

