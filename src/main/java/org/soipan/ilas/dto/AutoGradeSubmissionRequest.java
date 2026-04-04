package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instructor request payload to trigger LLM-based grading for a submission.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoGradeSubmissionRequest {
    private Integer instructorId;
}

