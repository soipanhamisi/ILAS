package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating an exam
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExamRequest {
    private Integer instructorId;
    private Integer courseId;
    private String examTitle;
    private Integer maxScore;
    // Note: CSV file is passed as MultipartFile separately
}

