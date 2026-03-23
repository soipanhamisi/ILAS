package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for browser-based exam submissions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitExamRequest {
    private Integer studentId;
    private List<String> questionAnswers;
}

