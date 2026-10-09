package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating feedback
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeedbackRequest {
    private String feedback;
    private String gradeJustification;
}

