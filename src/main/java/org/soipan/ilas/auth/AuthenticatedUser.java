package org.soipan.ilas.auth;

public record AuthenticatedUser(int userId, String username, String role) {
}
