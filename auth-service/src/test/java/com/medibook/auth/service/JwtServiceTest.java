package com.medibook.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.medibook.auth.entity.User;
import com.medibook.auth.service.serviceimpl.JwtService;

class JwtServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef";

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 60_000L);
    }

    @Test
    void generatedUserTokenExposesSubjectRoleUserIdAndName() {
        User user = new User();
        user.setId(42L);
        user.setEmail("patient@example.com");
        user.setName("Patient One");
        user.setRole("PATIENT");

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("patient@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("PATIENT");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.isTokenValid(token, "patient@example.com")).isTrue();
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void generatedEmailTokenIsValidAndInvalidTokenReturnsFalse() {
        String token = jwtService.generateToken("doctor@example.com", "Doctor");

        assertThat(jwtService.extractUsername(token)).isEqualTo("doctor@example.com");
        assertThat(jwtService.isTokenValid("bad-token")).isFalse();
    }
}
