package org.soipan.ilas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "admins_tbl")
public class Admin {
    @Id @GeneratedValue
    private int adminId;
    private String name;
    private String email;
    private String username;
    private String password;

    public Admin(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Admin(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}

