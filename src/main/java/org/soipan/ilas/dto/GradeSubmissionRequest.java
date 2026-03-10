package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for grading a submission
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeSubmissionRequest {
    private Integer instructorId;
    private Integer grade;
    private String feedback;
    private String gradeJustification;
}

