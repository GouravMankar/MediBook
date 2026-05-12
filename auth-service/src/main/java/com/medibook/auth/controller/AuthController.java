package com.medibook.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.auth.dto.AuthResponse;
import com.medibook.auth.dto.ForgotPasswordRequest;
import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.dto.LoginResponse;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.dto.ResetPasswordRequest;
import com.medibook.auth.dto.VerifyOtpRequest;
import com.medibook.auth.entity.User;
import com.medibook.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request.getEmail(), request.getPassword())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String email) {
        authService.logout(email);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam String token) {
        return ResponseEntity.ok(authService.refreshToken(token));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/profile/update/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(authService.updateProfile(id, user));
    }

    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<Map<String, String>> requestPasswordOtp(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent to registered email"));
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<Map<String, String>> verifyPasswordOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyPasswordOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(Map.of("message", "OTP verified"));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<String> changePassword(@PathVariable Long id,
                                                 @RequestBody Map<String, String> request) {
        authService.changePassword(id, request.get("newPassword"));
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateAccount(@PathVariable Long id) {
        authService.deactivateAccount(id);
        return ResponseEntity.ok("Account deactivated successfully");
    }
}
