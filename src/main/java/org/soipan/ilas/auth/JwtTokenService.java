package org.soipan.ilas.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenService {
    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtTokenService(@Value("${security.jwt.secret}") String base64Secret,
                           @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(base64Secret);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("security.jwt.secret must be a Base64-encoded key", ex);
        }
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("security.jwt.secret must decode to at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(int userId, String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("uid", userId)
                .claim("role", role.toLowerCase())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public Optional<AuthenticatedUser> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Number userId = claims.get("uid", Number.class);
            String role = claims.get("role", String.class);
            String username = claims.getSubject();
            if (userId == null || username == null || role == null
                    || !role.matches("student|instructor|admin")) {
                return Optional.empty();
            }
            return Optional.of(new AuthenticatedUser(userId.intValue(), username, role));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
