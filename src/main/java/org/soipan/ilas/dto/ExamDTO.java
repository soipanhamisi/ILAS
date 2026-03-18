package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Exam entity
 * Used to transfer exam data without exposing internal entity structure
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private Long examId;
    private String examTitle;
    private int maxScore;
    private String csvFilePath;

    // Course information
    private Integer courseId;
    private String courseTitle;

    // Instructor information
    private Integer instructorId;
    private String instructorName;
}

