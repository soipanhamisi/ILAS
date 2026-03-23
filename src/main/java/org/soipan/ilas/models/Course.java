package org.soipan.ilas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses_tbl")
public class Course {
    @Id
    private int courseId;
    private String courseTitle;

    @ManyToOne
    @JoinColumn(name = "instructorId")
    private Instructor instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course")
    private List<Exam> exams;

    public Course(String courseTitle, Instructor instructor) {
        this.courseTitle = courseTitle;
        this.instructor = instructor;
    }
}
