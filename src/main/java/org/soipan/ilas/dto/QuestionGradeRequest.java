package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-question grading request payload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionGradeRequest {
    private Integer questionNumber;
    private Integer grade;
    private String feedback;
    private String gradeJustification;
}

