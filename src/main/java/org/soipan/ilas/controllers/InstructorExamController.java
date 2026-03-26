package org.soipan.ilas.controllers;

import org.soipan.ilas.dto.*;
import org.soipan.ilas.models.Exam;
import org.soipan.ilas.models.ExamSubmission;
import org.soipan.ilas.services.InstructorExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Instructor Exam Operations
 * Handles exam creation, grading, and feedback management
 */
@RestController
@RequestMapping("/api/instructor/exams")
@CrossOrigin(origins = "*")
public class InstructorExamController {

    @Autowired
    private InstructorExamService examService;

    @Autowired
    private ExamMapper examMapper;

    /**
     * Create a new exam with CSV template
     * POST /api/instructor/exams/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ExamDTO>> createExam(
            @RequestParam("instructorId") Integer instructorId,
            @RequestParam("courseId") Integer courseId,
            @RequestParam("examTitle") String examTitle,
            @RequestParam("maxScore") Integer maxScore,
            @RequestParam("file") MultipartFile file) {

        Exam exam = examService.createAssessment(
                instructorId,
                courseId,
                examTitle,
                maxScore,
                file
        );

        ExamDTO examDTO = examMapper.toDTO(exam);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam created successfully", examDTO));
    }

    /**
     * Grade a student's exam submission
     * POST /api/instructor/exams/submissions/{submissionId}/grade
     */
    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<ApiResponse<ExamSubmissionDTO>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeSubmissionRequest request) {

        if (request == null || request.getInstructorId() == null) {
            throw new IllegalArgumentException("instructorId is required");
        }

        if (request.getQuestionGrades() == null || request.getQuestionGrades().isEmpty()) {
            throw new IllegalArgumentException("questionGrades is required");
        }

        List<QuestionGradeRequest> questionGrades = request.getQuestionGrades().stream()
                .sorted(Comparator.comparingInt(QuestionGradeRequest::getQuestionNumber))
                .toList();

        int totalGrade = 0;
        StringBuilder feedbackBuilder = new StringBuilder();
        StringBuilder justificationBuilder = new StringBuilder();

        for (int i = 0; i < questionGrades.size(); i++) {
            QuestionGradeRequest questionGrade = questionGrades.get(i);
            int expectedQuestionNumber = i + 1;

            if (questionGrade.getQuestionNumber() == null || questionGrade.getQuestionNumber() != expectedQuestionNumber) {
                throw new IllegalArgumentException("Question numbering is invalid at question " + expectedQuestionNumber);
            }

            if (questionGrade.getGrade() == null || questionGrade.getGrade() < 0) {
                throw new IllegalArgumentException("Grade for question " + expectedQuestionNumber + " is invalid");
            }

            if (questionGrade.getFeedback() == null || questionGrade.getFeedback().trim().isEmpty()) {
                throw new IllegalArgumentException("Feedback for question " + expectedQuestionNumber + " is required");
            }

            if (questionGrade.getGradeJustification() == null
                    || questionGrade.getGradeJustification().trim().isEmpty()) {
                throw new IllegalArgumentException("Justification for question " + expectedQuestionNumber + " is required");
            }

            totalGrade += questionGrade.getGrade();

            if (i > 0) {
                feedbackBuilder.append("\n\n");
                justificationBuilder.append("\n\n");
            }

            feedbackBuilder
                    .append("Q")
                    .append(expectedQuestionNumber)
                    .append(" Feedback: ")
                    .append(questionGrade.getFeedback().trim());

            justificationBuilder
                    .append("Q")
                    .append(expectedQuestionNumber)
                    .append(" Justification: ")
                    .append(questionGrade.getGradeJustification().trim());
        }

        ExamSubmission graded = examService.issueGrade(
                request.getInstructorId(),
                submissionId,
                totalGrade,
                feedbackBuilder.toString(),
                justificationBuilder.toString()
        );

        ExamSubmissionDTO dto = examMapper.toDTO(graded);
        return ResponseEntity.ok(ApiResponse.success("Submission graded successfully", dto));
    }

    /**
     * Update feedback for a graded submission
     * PUT /api/instructor/exams/submissions/{submissionId}/feedback
     */
    @PutMapping("/submissions/{submissionId}/feedback")
    public ResponseEntity<ApiResponse<ExamSubmissionDTO>> updateFeedback(
            @PathVariable Long submissionId,
            @RequestBody UpdateFeedbackRequest request) {

        ExamSubmission updated = examService.updateFeedback(
                request.getInstructorId(),
                submissionId,
                request.getFeedback(),
                request.getGradeJustification()
        );

        ExamSubmissionDTO dto = examMapper.toDTO(updated);
        return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully", dto));
    }

    /**
     * Get all submissions for an exam
     * GET /api/instructor/exams/{examId}/submissions
     */
    @GetMapping("/{examId}/submissions")
    public ResponseEntity<ApiResponse<List<ExamSubmissionDTO>>> getSubmissionsForExam(
            @PathVariable Long examId,
            @RequestParam Integer instructorId) {

        List<ExamSubmission> submissions = examService.getSubmissionsForExam(instructorId, examId);

        List<ExamSubmissionDTO> dtos = submissions.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get ungraded submissions for an exam
     * GET /api/instructor/exams/{examId}/submissions/ungraded
     */
    @GetMapping("/{examId}/submissions/ungraded")
    public ResponseEntity<ApiResponse<List<ExamSubmissionDTO>>> getUngradedSubmissions(
            @PathVariable Long examId,
            @RequestParam Integer instructorId) {

        List<ExamSubmission> submissions = examService.getUngradedSubmissions(instructorId, examId);

        List<ExamSubmissionDTO> dtos = submissions.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get all exams for a course
     * GET /api/instructor/courses/{courseId}/exams
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getExamsForCourse(
            @PathVariable Integer courseId,
            @RequestParam Integer instructorId) {

        List<Exam> exams = examService.getExamsForCourse(instructorId, courseId);

        List<ExamDTO> dtos = exams.stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Get instructor dashboard summary.
     * GET /api/instructor/exams/dashboard/summary
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<InstructorDashboardSummaryDTO>> getDashboardSummary(
            @RequestParam Integer instructorId) {

        InstructorDashboardSummaryDTO summary = examService.getDashboardSummary(instructorId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * Get exam questions for grading preview
     * GET /api/instructor/exams/{examId}/questions
     */
    @GetMapping("/{examId}/questions")
    public ResponseEntity<ApiResponse<List<String>>> getExamQuestions(
            @PathVariable Long examId,
            @RequestParam Integer instructorId) {

        List<String> questions = examService.getExamQuestions(instructorId, examId);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    /**
     * Get exam questions with max grade details from CSV.
     * GET /api/instructor/exams/{examId}/questions/details
     */
    @GetMapping("/{examId}/questions/details")
    public ResponseEntity<ApiResponse<List<ExamQuestionDTO>>> getExamQuestionDetails(
            @PathVariable Long examId,
            @RequestParam Integer instructorId) {

        List<ExamQuestionDTO> questions = examService.getExamQuestionDetails(instructorId, examId);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
}

