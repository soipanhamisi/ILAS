package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Instructor request payload for storing rubrics on an exam.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RubricDefinitionRequest {
    private Integer instructorId;
    private List<QuestionRubricDTO> rubrics;
}

