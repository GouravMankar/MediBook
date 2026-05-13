package com.medibook.auth.service.serviceimpl;

import java.time.LocalDateTime;
import java.security.SecureRandom;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.medibook.auth.client.NotificationClient;
import com.medibook.auth.dto.AuthResponse;
import com.medibook.auth.dto.EmailRequestDTO;
import com.medibook.auth.dto.LoginResponse;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.entity.PasswordResetOtp;
import com.medibook.auth.entity.User;
import com.medibook.auth.repository.PasswordResetOtpRepository;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetOtpRepository otpRepository;
    private final NotificationClient notificationClient;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (request == null) {
            throw new RuntimeException("Register request cannot be null");
        }

        if (!StringUtils.hasText(request.getFullName())) {
            throw new RuntimeException("Full name is required");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new RuntimeException("Email is required");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("Password is required");
        }

        if (!StringUtils.hasText(request.getPhone())) {
            throw new RuntimeException("Phone is required");
        }

        if (!StringUtils.hasText(request.getRole())) {
            throw new RuntimeException("Role is required");
        }

        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new RuntimeException("Phone already registered");
        }

        User user = new User();
        user.setName(request.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(phone);
        user.setRole(request.getRole().trim().toUpperCase());
        user.setProvider(
                StringUtils.hasText(request.getProvider())
                        ? request.getProvider().trim().toUpperCase()
                        : "LOCAL"
        );
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setProfilePicUrl(request.getProfilePicUrl());

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public LoginResponse login(String email, String password) {

        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new RuntimeException("Email and password are required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, password)
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is deactivated");
        }
        

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .token(jwtService.generateToken(user))
                .build();
    }

    @Override
    public void logout(String email) {

        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is already deactivated");
        }

    }

    @Override
    public boolean isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }
    @Override
    public String refreshToken(String token) {

        if (!StringUtils.hasText(token)) {
            throw new RuntimeException("Token is required");
        }

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is deactivated");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public User getUserByEmail(String email) {

        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email is required");
        }

        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User getUserById(Long id) {

        if (id == null) {
            throw new RuntimeException("User id is required");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User updateProfile(Long id, User user) {

        if (id == null) {
            throw new RuntimeException("User id is required");
        }

        if (user == null) {
            throw new RuntimeException("User payload cannot be null");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (StringUtils.hasText(user.getName())) {
            existingUser.setName(user.getName().trim());
        }

        if (StringUtils.hasText(user.getEmail())) {
            String newEmail = user.getEmail().trim().toLowerCase();

            userRepository.findByEmail(newEmail).ifPresent(foundUser -> {
                if (!foundUser.getId().equals(existingUser.getId())) {
                    throw new RuntimeException("Email already in use");
                }
            });

            existingUser.setEmail(newEmail);
        }

        if (StringUtils.hasText(user.getPhone())) {
            String newPhone = user.getPhone().trim();

            userRepository.findByPhone(newPhone).ifPresent(foundUser -> {
                if (!foundUser.getId().equals(existingUser.getId())) {
                    throw new RuntimeException("Phone number already in use");
                }
            });

            existingUser.setPhone(newPhone);
        }

        if (StringUtils.hasText(user.getProfilePicUrl())) {
            existingUser.setProfilePicUrl(user.getProfilePicUrl().trim());
        }



        return userRepository.save(existingUser);
    }

    @Override
    public void changePassword(Long id, String newPassword) {

        if (id == null) {
            throw new RuntimeException("User id is required");
        }

        if (!StringUtils.hasText(newPassword)) {
            throw new RuntimeException("New password is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
    }

    @Override
    public void deactivateAccount(Long id) {

        if (id == null) {
            throw new RuntimeException("User id is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void requestPasswordOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("No account found for this email"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is deactivated");
        }

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        PasswordResetOtp passwordResetOtp = PasswordResetOtp.builder()
                .email(normalizedEmail)
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .verified(false)
                .used(false)
                .build();

        otpRepository.save(passwordResetOtp);
        sendOtpEmail(normalizedEmail, otp);
    }

    @Override
    public void verifyPasswordOtp(String email, String otp) {
        PasswordResetOtp passwordResetOtp = getActiveOtp(email, otp);
        passwordResetOtp.setVerified(true);
        otpRepository.save(passwordResetOtp);
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new RuntimeException("New password is required");
        }

        PasswordResetOtp passwordResetOtp = getActiveOtp(email, otp);

        if (!Boolean.TRUE.equals(passwordResetOtp.getVerified())) {
            throw new RuntimeException("OTP must be verified before resetting password");
        }

        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);

        passwordResetOtp.setUsed(true);
        otpRepository.save(passwordResetOtp);
    }

    @Override
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            String email = jwtService.extractUsername(token);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return jwtService.isTokenValid(token, user.getEmail()) 
                   && Boolean.TRUE.equals(user.getIsActive());

        } catch (Exception e) {
            return false;
        }
    }

    private PasswordResetOtp getActiveOtp(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);
        PasswordResetOtp passwordResetOtp = otpRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("OTP not found. Request a new OTP."));

        if (passwordResetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Request a new OTP.");
        }

        if (!passwordEncoder.matches(otp, passwordResetOtp.getOtpHash())) {
            throw new RuntimeException("Invalid OTP");
        }

        return passwordResetOtp;
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email is required");
        }
        return email.trim().toLowerCase();
    }

    private void sendOtpEmail(String email, String otp) {
        try {
            notificationClient.sendEmail(new EmailRequestDTO(
                    email,
                    "MediBook password reset OTP",
                    "Your MediBook password reset OTP is " + otp + ". It expires in 10 minutes."
            ));
        } catch (Exception e) {
            log.warn("Notification service email failed. Password reset OTP for {} is {}", email, otp, e);
            throw new RuntimeException("OTP generated but email could not be sent. Please ensure notification-service is running and mail settings are valid.");
        }
    }
}
