package org.soipan.ilas.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Course {
    private int courseId;
    private String courseTitle;
    private int instructorId;

    public Course(String courseTitle, int instructorId) {
        this.courseTitle = courseTitle;
        this.instructorId = instructorId;
    }
}
