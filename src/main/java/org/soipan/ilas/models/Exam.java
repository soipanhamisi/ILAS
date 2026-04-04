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
@Table(name = "exams_tbl")
public class Exam {
    @Id
    @GeneratedValue
    private Long examId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String examTitle;
    private int maxScore;
    private String csvFilePath;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String gradingRubricsJson;

    private LocalDateTime gradingRubricsUpdatedAt;

    private Integer gradingRubricsVersion;

    public Exam(Course course, String examTitle, int maxScore, String csvFilePath) {
        this.course = course;
        this.examTitle = examTitle;
        this.maxScore = maxScore;
        this.csvFilePath = csvFilePath;
    }
}
