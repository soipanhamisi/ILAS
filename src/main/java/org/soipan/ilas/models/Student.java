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
@Table(name = "students_tbl")
public class Student {
    @Id @GeneratedValue
    private int studentId;
    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
