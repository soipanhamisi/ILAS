package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ExamSubmission entity
 * Contains submission details, grade, and feedback
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmissionDTO {
    private Long submissionId;

    // Student information
    private Integer studentId;
    private String studentName;

    // Exam information
    private Long examId;
    private String examTitle;
    private int maxScore;

    // Submission details
    private String submissionCsvPath;
    private String submissionText;
    private LocalDateTime submittedAt;

    // Grading information
    private Integer grade;
    private String feedback;
    private String gradeJustification;
    private LocalDateTime gradedAt;

    // Grader information
    private Integer gradedByInstructorId;
    private String gradedByInstructorName;
}

