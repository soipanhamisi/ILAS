package org.soipan.ilas.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Instructor {
    private int instructorId;
    private String name;
    private String email;

    public Instructor(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
