package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for instructor dashboard aggregate metrics and rollups.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDashboardSummaryDTO {
    private int coursesTaught;
    private int newEnrollments;
    private double averagePerformancePct;
    private int testsToBeGraded;
    private List<DailyActiveStudentsPointDTO> activeStudentsTrend;
    private List<CourseSummaryDTO> courses;
    private List<GradingQueueItemDTO> testsToGrade;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseSummaryDTO {
        private int courseId;
        private String courseTitle;
        private int enrollmentCount;
        private int newEnrollments;
        private double averagePerformancePct;
        private int testsToBeGraded;
        private List<DailyActiveStudentsPointDTO> activeStudentsTrend;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyActiveStudentsPointDTO {
        private String date;
        private int activeStudents;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GradingQueueItemDTO {
        private Long examId;
        private String examTitle;
        private int courseId;
        private String courseTitle;
        private int ungradedCount;
    }
}

