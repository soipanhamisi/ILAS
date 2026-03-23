package org.soipan.ilas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auth response DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Integer userId;
    private String name;
    private String username;
    private String email;
    private String userType; // 'instructor' or 'student'
    private String token; // JWT or simple token
}

