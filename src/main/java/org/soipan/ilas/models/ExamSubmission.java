package org.soipan.ilas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exam_submissions_tbl")
public class ExamSubmission {
    @Id
    @GeneratedValue
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "examId")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private String submissionCsvPath; // Path to student's submitted CSV file
    private LocalDateTime submittedAt;

    private Integer grade; // Actual grade received
    private String feedback; // Instructor comments
    private String gradeJustification; // Detailed grading explanation
    private LocalDateTime gradedAt;

    @ManyToOne
    @JoinColumn(name = "gradedByInstructorId")
    private Instructor gradedBy;

    public ExamSubmission(Exam exam, Student student, String submissionCsvPath) {
        this.exam = exam;
        this.student = student;
        this.submissionCsvPath = submissionCsvPath;
        this.submittedAt = LocalDateTime.now();
    }
}

