package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a parsed exam question and its max grade from CSV.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDTO {
    private Integer questionNumber;
    private String questionText;
    private Integer maxGrade;
}

