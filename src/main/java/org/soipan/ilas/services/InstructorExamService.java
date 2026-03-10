package org.soipan.ilas.services;

import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
    private FileStorageService fileStorageService;

    /**
     * Instructor Use Case 1: Create Assessment
     * Instructor creates an exam with CSV template for a course
     */
    @Transactional
    public Exam createAssessment(int instructorId, int courseId, String examTitle,
                                  int maxScore, MultipartFile csvFile) {
        // Verify instructor exists
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
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
    public ExamSubmission issueGrade(int instructorId, Long submissionId, int grade,
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

        // Validate grade
        int maxScore = submission.getExam().getMaxScore();
        if (grade < 0 || grade > maxScore) {
            throw new IllegalArgumentException("Grade must be between 0 and " + maxScore);
        }

        // Set grade and feedback
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
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
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
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
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
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        // Verify course exists and belongs to instructor
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own this course");
        }

        return examRepository.findByCourse_CourseId(courseId);
    }
}

