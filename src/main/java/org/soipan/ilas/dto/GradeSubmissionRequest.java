package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for grading a submission
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeSubmissionRequest {
    private Integer instructorId;
    private List<QuestionGradeRequest> questionGrades;
}

