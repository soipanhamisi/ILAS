package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for course enrollment views
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEnrollmentDTO {
    private int courseId;
    private String courseTitle;
    private Integer instructorId;
    private String instructorName;
    private boolean enrolled;
}

