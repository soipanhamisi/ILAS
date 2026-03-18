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

        ExamSubmission graded = examService.issueGrade(
                request.getInstructorId(),
                submissionId,
                request.getGrade(),
                request.getFeedback(),
                request.getGradeJustification()
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
}

