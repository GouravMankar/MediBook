package com.medibook.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef";

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
    }

    @Test
    void extractsUsernameRoleAndUserIdFromValidToken() {
        String token = tokenWithExpiry(new Date(System.currentTimeMillis() + 60_000), 42L);

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("patient@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("PATIENT");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    void expiredTokenIsInvalid() {
        String token = tokenWithExpiry(new Date(System.currentTimeMillis() - 60_000), 42L);

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void malformedTokenIsInvalid() {
        assertThat(jwtService.isTokenValid("not-a-jwt")).isFalse();
    }

    @Test
    void extractUserIdSupportsIntegerStringAndMissingClaim() {
        Date expiry = new Date(System.currentTimeMillis() + 60_000);

        assertThat(jwtService.extractUserId(tokenWithExpiry(expiry, 42))).isEqualTo(42L);
        assertThat(jwtService.extractUserId(tokenWithExpiry(expiry, "42"))).isEqualTo(42L);
        assertThat(jwtService.extractUserId(tokenWithExpiry(expiry, null))).isNull();
    }

    private String tokenWithExpiry(Date expiry, Object userId) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        var builder = Jwts.builder()
                .subject("patient@example.com")
                .claim("role", "PATIENT")
                .expiration(expiry);

        if (userId != null) {
            builder.claim("userId", userId);
        }

        return builder.signWith(key).compact();
    }
}
