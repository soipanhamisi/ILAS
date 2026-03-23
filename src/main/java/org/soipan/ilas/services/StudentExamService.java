package org.soipan.ilas.services;

import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * <h1>Service for Student-related exam operations</h1><br>
 * Purpose: Student submits their completed exam answer text<br>
 *
 * @params: studentId - ID of the student,
 * examId - ID of the exam,
 * questionAnswers - Student answers in question order,
 * @returns: ExamSubmission object<br>
 *
 * @validations: Student must exist,
 * Exam must exist,
 * Student must be enrolled in the course,
 * Student cannot submit duplicate exams
 */
@Service
public class StudentExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSubmissionRepository submissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Student Use Case 1: Take Assessment and Submit
     * Student submits their completed exam in-browser as text
     */
    @Transactional
    public ExamSubmission submitAssessment(int studentId, Long examId, List<String> questionAnswers) {
        if (questionAnswers == null || questionAnswers.isEmpty()) {
            throw new IllegalArgumentException("At least one answer is required");
        }

        boolean hasBlankAnswer = questionAnswers.stream()
                .anyMatch(answer -> answer == null || answer.trim().isEmpty());
        if (hasBlankAnswer) {
            throw new IllegalArgumentException("Please answer all questions before submitting");
        }

        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Verify exam exists
        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        // Verify student is enrolled in the course
        Course course = exam.getCourse();
        enrollmentRepository
                .findByStudent_StudentIdAndCourse_CourseIdAndEnrollmentStatus(
                        studentId,
                        course.getCourseId(),
                        EnrollmentStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student is not enrolled in the course for this exam"));

        // Check if student has already submitted this exam
        if (submissionRepository.existsByStudent_StudentIdAndExam_ExamId(studentId, examId)) {
            throw new IllegalArgumentException("Student has already submitted this exam");
        }

        String formattedSubmission = IntStream.range(0, questionAnswers.size())
                .mapToObj(index -> "Q" + (index + 1) + ":\n" + questionAnswers.get(index).trim())
                .reduce((left, right) -> left + "\n\n" + right)
                .orElseThrow(() -> new IllegalArgumentException("At least one answer is required"));

        // Create and save submission
        ExamSubmission submission = new ExamSubmission(exam, student, formattedSubmission, true);
        return submissionRepository.save(submission);
    }

    /**
     * Student Use Case 2: Receive Grade and Feedback
     * Student views their graded submission with feedback
     */
    public ExamSubmission getGradeAndFeedback(int studentId, Long examId) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Find submission
        ExamSubmission submission = submissionRepository
                .findByStudent_StudentIdAndExam_ExamId(studentId, examId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No submission found for this exam"));

        return submission;
    }

    /**
     * Get all submissions for a student (across all exams)
     */
    public List<ExamSubmission> getAllSubmissionsForStudent(int studentId) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        return submissionRepository.findByStudent_StudentId(studentId);
    }

    /**
     * Get all graded submissions for a student
     */
    public List<ExamSubmission> getGradedSubmissionsForStudent(int studentId) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        List<ExamSubmission> allSubmissions = submissionRepository.findByStudent_StudentId(studentId);
        return allSubmissions.stream()
                .filter(submission -> submission.getGrade() != null)
                .toList();
    }

    /**
     * Get all available exams for a student (based on enrollments)
     */
    public List<Exam> getAvailableExams(int studentId) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Get all enrollments for student
        List<Enrollment> enrollments = enrollmentRepository
                .findByStudent_StudentIdAndEnrollmentStatus(studentId, EnrollmentStatus.ACTIVE);

        // Get exams from enrolled courses
        return enrollments.stream()
                .flatMap(enrollment ->
                    examRepository.findByCourse_CourseId(enrollment.getCourse().getCourseId()).stream())
                .toList();
    }

    /**
     * Check if student has submitted an exam
     */
    public boolean hasSubmitted(int studentId, Long examId) {
        return submissionRepository.existsByStudent_StudentIdAndExam_ExamId(studentId, examId);
    }

    /**
     * Get exam details (for student to view before taking)
     */
    public Exam getExamDetails(int studentId, Long examId) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Verify exam exists
        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        // Verify student is enrolled in the course
        Course course = exam.getCourse();
        enrollmentRepository
                .findByStudent_StudentIdAndCourse_CourseIdAndEnrollmentStatus(
                        studentId,
                        course.getCourseId(),
                        EnrollmentStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student is not enrolled in the course for this exam"));

        return exam;
    }

    /**
     * Get all courses for enrollment browsing
     */
    public List<Course> getAllCoursesForStudent(int studentId) {
        studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Course::getCourseTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Get active enrollments for a student
     */
    public List<Course> getEnrolledCourses(int studentId) {
        studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        return enrollmentRepository
                .findByStudent_StudentIdAndEnrollmentStatus(studentId, EnrollmentStatus.ACTIVE)
                .stream()
                .map(Enrollment::getCourse)
                .sorted(Comparator.comparing(Course::getCourseTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Enroll a student in a course
     */
    @Transactional
    public Enrollment enrollStudentInCourse(int studentId, int courseId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Enrollment existingEnrollment = enrollmentRepository
                .findByStudent_StudentIdAndCourse_CourseId(studentId, courseId)
                .orElse(null);

        if (existingEnrollment != null) {
            if (existingEnrollment.getEnrollmentStatus() == EnrollmentStatus.ACTIVE) {
                throw new IllegalArgumentException("Student is already enrolled in this course");
            }

            existingEnrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
            return enrollmentRepository.save(existingEnrollment);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Get exam questions for display in the student UI.
     */
    public List<String> getExamQuestions(int studentId, Long examId) {
        Exam exam = getExamDetails(studentId, examId);

        String csvPath = exam.getCsvFilePath();
        if (csvPath == null || csvPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam questions are not available");
        }

        try {
            Path path = Paths.get(csvPath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Exam question file was not found");
            }

            return Files.readAllLines(path).stream()
                    .skip(1)
                    .map(this::extractFirstCsvColumn)
                    .map(String::trim)
                    .filter(question -> !question.isEmpty())
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read exam questions", ex);
        }
    }

    private String extractFirstCsvColumn(String line) {
        if (line == null || line.isBlank()) {
            return "";
        }

        StringBuilder firstColumn = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);

            if (current == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    firstColumn.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (current == ',' && !inQuotes) {
                break;
            }

            firstColumn.append(current);
        }

        String value = firstColumn.toString().trim();
        if (value.toLowerCase(Locale.ROOT).startsWith("question")) {
            return "";
        }
        return value;
    }
}

