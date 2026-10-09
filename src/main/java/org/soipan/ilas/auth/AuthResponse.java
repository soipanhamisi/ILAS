package org.soipan.ilas.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Integer userId;
    private String name;
    private String username;
    private String email;
    private String userType;
    private String token;
}
