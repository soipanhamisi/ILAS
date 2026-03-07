package org.soipan.ilas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "instructors_tbl")
public class Instructor {
    @Id @GeneratedValue
    private int instructorId;
    private String name;
    private String email;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private List<Course> courses;

    public Instructor() {
    }

    public Instructor(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
