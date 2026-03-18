package org.soipan.ilas.services;

import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <h1>Service for Student-related exam operations</h1><br>
 * Purpose: Student submits their completed exam as CSV file<br>
 *
 * @params: studentId - ID of the student,
 * examId - ID of the exam,
 * csvFile - Completed exam CSV file,
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
    private FileStorageService fileStorageService;

    /**
     * Student Use Case 1: Take Assessment and Submit
     * Student submits their completed exam as CSV file
     */
    @Transactional
    public ExamSubmission submitAssessment(int studentId, Long examId, MultipartFile csvFile) {
        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Verify exam exists
        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        // Verify student is enrolled in the course
        Course course = exam.getCourse();
        Enrollment enrollment = enrollmentRepository
                .findByStudent_StudentIdAndCourse_CourseId(studentId, course.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student is not enrolled in the course for this exam"));

        // Check if student has already submitted this exam
        if (submissionRepository.existsByStudent_StudentIdAndExam_ExamId(studentId, examId)) {
            throw new IllegalArgumentException("Student has already submitted this exam");
        }

        // Store the submitted CSV file
        String submissionCsvPath = fileStorageService.storeFile(
                csvFile,
                "submission_student" + studentId + "_exam" + examId
        );

        // Create and save submission
        ExamSubmission submission = new ExamSubmission(exam, student, submissionCsvPath);
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
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_StudentId(studentId);

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
                .findByStudent_StudentIdAndCourse_CourseId(studentId, course.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student is not enrolled in the course for this exam"));

        return exam;
    }
}

