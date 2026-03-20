package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signup request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private String userType; // 'instructor' or 'student'
}

