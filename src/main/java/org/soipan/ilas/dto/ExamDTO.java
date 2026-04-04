package org.soipan.ilas.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Exam entity
 * Used to transfer exam data without exposing internal entity structure
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private Long examId;
    private String examTitle;
    private int maxScore;
    private String csvFilePath;
    private String gradingRubricsJson;
    private LocalDateTime gradingRubricsUpdatedAt;
    private Integer gradingRubricsVersion;

    // Course information
    private Integer courseId;
    private String courseTitle;

    // Instructor information
    private Integer instructorId;
    private String instructorName;

    /**
     * Computed field indicating whether grading rubrics have been defined for this exam.
     * Returns true if gradingRubricsJson is not null and not empty/blank.
     */
    public boolean isRubricsDefined() {
        return gradingRubricsJson != null && !gradingRubricsJson.trim().isEmpty();
    }
}

