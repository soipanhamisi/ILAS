package org.soipan.ilas.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Enrollment {
    private int courseId;
    private int studentId;
    private int grade;
    private EnrollmentStatus enrollmentStatus;
}
