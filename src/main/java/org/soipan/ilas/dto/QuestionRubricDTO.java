package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-question rubric definition used by instructors and the auto-grading service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRubricDTO {
    private Integer questionNumber;
    private Integer maxScore;
    private String rubricText;
}

