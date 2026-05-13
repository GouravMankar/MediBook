package com.medibook.auth.service;

import com.medibook.auth.dto.AuthResponse;
import com.medibook.auth.dto.LoginResponse;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.entity.User;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    LoginResponse login(String email, String password);

    void logout(String email);

    boolean validateToken(String token);

    String refreshToken(String token);

    User getUserByEmail(String email);

    User getUserById(Long id);
   
    boolean isTokenValid(String token);

    User updateProfile(Long id, User user);

    void changePassword(Long id, String newPassword);

    void deactivateAccount(Long id);

    void requestPasswordOtp(String email);

    void verifyPasswordOtp(String email, String otp);

    void resetPassword(String email, String otp, String newPassword);
}
