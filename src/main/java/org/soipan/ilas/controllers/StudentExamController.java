package org.soipan.ilas.controllers;

import org.soipan.ilas.dto.ApiResponse;
import org.soipan.ilas.dto.ExamDTO;
import org.soipan.ilas.dto.ExamMapper;
import org.soipan.ilas.dto.ExamSubmissionDTO;
import org.soipan.ilas.dto.SubmitExamRequest;
import org.soipan.ilas.models.Exam;
import org.soipan.ilas.models.ExamSubmission;
import org.soipan.ilas.services.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Student Exam Operations
 * Handles exam submission and grade viewing
 */
@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    @Autowired
    private StudentExamService examService;

    @Autowired
    private ExamMapper examMapper;

    /**
     * Submit an exam
     * POST /api/student/exams/{examId}/submit
     */
    @PostMapping("/{examId}/submit")
    public ResponseEntity<ApiResponse<ExamSubmissionDTO>> submitExam(
            @PathVariable Long examId,
            @RequestBody SubmitExamRequest request) {

        if (request == null || request.getStudentId() == null) {
            throw new IllegalArgumentException("studentId is required");
        }

        if (request.getQuestionAnswers() == null || request.getQuestionAnswers().isEmpty()) {
            throw new IllegalArgumentException("questionAnswers is required");
        }

        ExamSubmission submission = examService.submitAssessment(
                request.getStudentId(),
                examId,
                request.getQuestionAnswers());

        ExamSubmissionDTO dto = examMapper.toDTO(submission);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam submitted successfully", dto));
    }

    /**
     * Get grade and feedback for an exam
     * GET /api/student/exams/{examId}/grade
     */
    @GetMapping("/{examId}/grade")
    public ResponseEntity<ApiResponse<ExamSubmissionDTO>> getGradeAndFeedback(
            @PathVariable Long examId,
            @RequestParam Integer studentId) {

        ExamSubmission submission = examService.getGradeAndFeedback(studentId, examId);

        ExamSubmissionDTO dto = examMapper.toDTO(submission);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /**
     * Get all submissions for a student
     * GET /api/student/exams/submissions
     */
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<List<ExamSubmissionDTO>>> getAllSubmissions(
            @RequestParam Integer studentId) {

        List<ExamSubmission> submissions = examService.getAllSubmissionsForStudent(studentId);

        List<ExamSubmissionDTO> dtos = submissions.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get only graded submissions for a student
     * GET /api/student/exams/submissions/graded
     */
    @GetMapping("/submissions/graded")
    public ResponseEntity<ApiResponse<List<ExamSubmissionDTO>>> getGradedSubmissions(
            @RequestParam Integer studentId) {

        List<ExamSubmission> submissions = examService.getGradedSubmissionsForStudent(studentId);

        List<ExamSubmissionDTO> dtos = submissions.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get all available exams for a student (based on enrollments)
     * GET /api/student/exams/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getAvailableExams(
            @RequestParam Integer studentId) {

        List<Exam> exams = examService.getAvailableExams(studentId);

        List<ExamDTO> dtos = exams.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get exam details
     * GET /api/student/exams/{examId}
     */
    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponse<ExamDTO>> getExamDetails(
            @PathVariable Long examId,
            @RequestParam Integer studentId) {

        Exam exam = examService.getExamDetails(studentId, examId);

        ExamDTO dto = examMapper.toDTO(exam);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /**
     * Get exam questions for student preview.
     * GET /api/student/exams/{examId}/questions
     */
    @GetMapping("/{examId}/questions")
    public ResponseEntity<ApiResponse<List<String>>> getExamQuestions(
            @PathVariable Long examId,
            @RequestParam Integer studentId) {

        List<String> questions = examService.getExamQuestions(studentId, examId);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    /**
     * Check if student has submitted an exam
     * GET /api/student/exams/{examId}/submitted
     */
    @GetMapping("/{examId}/submitted")
    public ResponseEntity<ApiResponse<Boolean>> hasSubmitted(
            @PathVariable Long examId,
            @RequestParam Integer studentId) {

        boolean submitted = examService.hasSubmitted(studentId, examId);

        return ResponseEntity.ok(ApiResponse.success(submitted));
    }
}
