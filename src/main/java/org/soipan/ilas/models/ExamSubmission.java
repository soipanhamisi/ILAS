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

    private String submissionCsvPath; // Legacy field for older file-based submissions
    @Column(columnDefinition = "TEXT")
    private String submissionText; // Browser-entered answer text
    private LocalDateTime submittedAt;

    private Integer grade; // Actual grade received
    @Lob
    @Column(columnDefinition = "TEXT")
    private String feedback; // Instructor comments
    @Lob
    @Column(columnDefinition = "TEXT")
    private String gradeJustification; // Detailed grading explanation
    private LocalDateTime gradedAt;

    private String gradingSource;
    private String gradingModel;
    private Integer rubricVersionUsed;
    private Boolean requiresInstructorReview;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String autoGradingResultJson;

    @ManyToOne
    @JoinColumn(name = "gradedByInstructorId")
    private Instructor gradedBy;

    public ExamSubmission(Exam exam, Student student, String submissionCsvPath) {
        this.exam = exam;
        this.student = student;
        this.submissionCsvPath = submissionCsvPath;
        this.submittedAt = LocalDateTime.now();
    }

    public ExamSubmission(Exam exam, Student student, String submissionText, boolean textSubmission) {
        this.exam = exam;
        this.student = student;
        if (textSubmission) {
            this.submissionText = submissionText;
            this.submissionCsvPath = null;
        } else {
            this.submissionCsvPath = submissionText;
        }
        this.submittedAt = LocalDateTime.now();
    }
}

