package com.medibook.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medibook.auth.client.NotificationClient;
import com.medibook.auth.entity.PasswordResetOtp;
import com.medibook.auth.entity.User;
import com.medibook.auth.repository.PasswordResetOtpRepository;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.serviceimpl.AuthServiceImpl;
import com.medibook.auth.service.serviceimpl.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetOtpRepository otpRepository;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void profileUpdatePersistsEmailAndName() {
        User existing = user(1L, "old@example.com");
        User update = user(1L, "new@example.com");
        update.setName("New Name");
        update.setPhone("9876543210");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.updateProfile(1L, update);

        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getPhone()).isEqualTo("9876543210");
    }

    @Test
    void requestPasswordOtpStoresHashedOtpWithExpiry() {
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user(1L, "patient@example.com")));
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed-otp");

        service.requestPasswordOtp("patient@example.com");

        ArgumentCaptor<PasswordResetOtp> captor = ArgumentCaptor.forClass(PasswordResetOtp.class);
        verify(otpRepository).save(captor.capture());
        assertThat(captor.getValue().getOtpHash()).isEqualTo("hashed-otp");
        assertThat(captor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void resetPasswordRejectsUnverifiedOtp() {
        PasswordResetOtp otp = PasswordResetOtp.builder()
                .email("patient@example.com")
                .otpHash("hash")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .used(false)
                .build();
        when(otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("patient@example.com"))
                .thenReturn(Optional.of(otp));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);

        assertThatThrownBy(() ->
                service.resetPassword("patient@example.com", "123456", "Newpass@1"))
                .hasMessageContaining("verified");
    }

    @Test
    void registerNormalizesUserAndReturnsToken() {
        com.medibook.auth.dto.RegisterRequest request = new com.medibook.auth.dto.RegisterRequest();
        request.setFullName(" Patient One ");
        request.setEmail("PATIENT@EXAMPLE.COM ");
        request.setPassword("Pass@123");
        request.setPhone("9876543210");
        request.setRole("patient");
        when(userRepository.existsByEmail("patient@example.com")).thenReturn(false);
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass@123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

        com.medibook.auth.dto.AuthResponse response = service.register(request);

        assertThat(response.getToken()).isEqualTo("jwt");
        assertThat(response.getEmail()).isEqualTo("patient@example.com");
        assertThat(response.getRole()).isEqualTo("PATIENT");
    }

    @Test
    void registerRejectsMissingFieldsDuplicateEmailAndPhone() {
        assertThatThrownBy(() -> service.register(null))
                .hasMessageContaining("Register request");

        com.medibook.auth.dto.RegisterRequest request = new com.medibook.auth.dto.RegisterRequest();
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Full name");

        request.setFullName("Patient");
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Email");

        request.setEmail("patient@example.com");
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Password");

        request.setPassword("Pass@123");
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Phone");

        request.setPhone("9876543210");
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Role");

        request.setRole("PATIENT");
        when(userRepository.existsByEmail("patient@example.com")).thenReturn(true);
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Email already");

        when(userRepository.existsByEmail("patient@example.com")).thenReturn(false);
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.of(user(2L, "other@example.com")));
        assertThatThrownBy(() -> service.register(request))
                .hasMessageContaining("Phone already");
    }

    @Test
    void loginAuthenticatesActiveUserAndRejectsInactiveUser() {
        User user = user(1L, "patient@example.com");
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt");

        com.medibook.auth.dto.LoginResponse response = service.login(" PATIENT@example.com ", "pass");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("jwt");

        user.setIsActive(false);
        assertThatThrownBy(() -> service.login("patient@example.com", "pass"))
                .hasMessageContaining("deactivated");
    }

    @Test
    void refreshTokenValidatesTokenAndActiveUser() {
        User user = user(1L, "patient@example.com");
        when(jwtService.isTokenValid("old")).thenReturn(true);
        when(jwtService.extractUsername("old")).thenReturn("patient@example.com");
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new");

        assertThat(service.refreshToken("old")).isEqualTo("new");
    }

    @Test
    void logoutRejectsBlankMissingAndInactiveUsers() {
        assertThatThrownBy(() -> service.logout(" "))
                .hasMessageContaining("Email is required");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.logout("missing@example.com"))
                .hasMessageContaining("User not found");

        User inactive = user(2L, "inactive@example.com");
        inactive.setIsActive(false);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.logout("inactive@example.com"))
                .hasMessageContaining("already deactivated");
    }

    @Test
    void serviceTokenValidityDelegatesToJwtService() {
        when(jwtService.isTokenValid("token")).thenReturn(true);

        assertThat(service.isTokenValid("token")).isTrue();
    }

    @Test
    void refreshTokenRejectsBlankInvalidMissingAndInactiveUsers() {
        assertThatThrownBy(() -> service.refreshToken(" "))
                .hasMessageContaining("Token is required");

        when(jwtService.isTokenValid("bad")).thenReturn(false);
        assertThatThrownBy(() -> service.refreshToken("bad"))
                .hasMessageContaining("Invalid or expired");

        when(jwtService.isTokenValid("missing")).thenReturn(true);
        when(jwtService.extractUsername("missing")).thenReturn("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.refreshToken("missing"))
                .hasMessageContaining("User not found");

        User inactive = user(3L, "inactive@example.com");
        inactive.setIsActive(false);
        when(jwtService.isTokenValid("inactive")).thenReturn(true);
        when(jwtService.extractUsername("inactive")).thenReturn("inactive@example.com");
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.refreshToken("inactive"))
                .hasMessageContaining("deactivated");
    }

    @Test
    void changePasswordDeactivateAndLookupMethodsUseRepository() {
        User user = user(1L, "patient@example.com");
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("Newpass@1")).thenReturn("encoded-new");

        assertThat(service.getUserByEmail(" PATIENT@example.com ")).isSameAs(user);
        assertThat(service.getUserById(1L)).isSameAs(user);

        service.changePassword(1L, "Newpass@1");
        assertThat(user.getPassword()).isEqualTo("encoded-new");

        service.deactivateAccount(1L);
        assertThat(user.getIsActive()).isFalse();
        verify(userRepository, org.mockito.Mockito.times(2)).save(user);
    }

    @Test
    void lookupAndAccountChangesRejectInvalidOrMissingUsers() {
        assertThatThrownBy(() -> service.getUserByEmail(" "))
                .hasMessageContaining("Email is required");
        assertThatThrownBy(() -> service.getUserById(null))
                .hasMessageContaining("User id is required");
        assertThatThrownBy(() -> service.updateProfile(null, new User()))
                .hasMessageContaining("User id is required");
        assertThatThrownBy(() -> service.updateProfile(1L, null))
                .hasMessageContaining("payload");
        assertThatThrownBy(() -> service.changePassword(null, "Newpass@1"))
                .hasMessageContaining("User id is required");
        assertThatThrownBy(() -> service.changePassword(1L, " "))
                .hasMessageContaining("New password");
        assertThatThrownBy(() -> service.deactivateAccount(null))
                .hasMessageContaining("User id is required");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getUserByEmail("missing@example.com"))
                .hasMessageContaining("User not found");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getUserById(99L))
                .hasMessageContaining("User not found");
        assertThatThrownBy(() -> service.changePassword(99L, "Newpass@1"))
                .hasMessageContaining("User not found");
        assertThatThrownBy(() -> service.deactivateAccount(99L))
                .hasMessageContaining("User not found");
    }

    @Test
    void updateProfileRejectsDuplicateEmailAndPhoneFromAnotherUser() {
        User existing = user(1L, "patient@example.com");
        User update = new User();
        update.setEmail("used@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("used@example.com")).thenReturn(Optional.of(user(2L, "used@example.com")));

        assertThatThrownBy(() -> service.updateProfile(1L, update))
                .hasMessageContaining("Email already");

        update.setEmail(null);
        update.setPhone("9999999999");
        when(userRepository.findByPhone("9999999999")).thenReturn(Optional.of(user(2L, "other@example.com")));

        assertThatThrownBy(() -> service.updateProfile(1L, update))
                .hasMessageContaining("Phone number");
    }

    @Test
    void verifyAndResetPasswordOtpHappyPath() {
        PasswordResetOtp otp = PasswordResetOtp.builder()
                .email("patient@example.com")
                .otpHash("hash")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .used(false)
                .build();
        User user = user(1L, "patient@example.com");
        when(otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("patient@example.com"))
                .thenReturn(Optional.of(otp));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("Newpass@1")).thenReturn("encoded");

        service.verifyPasswordOtp("patient@example.com", "123456");
        assertThat(otp.getVerified()).isTrue();

        service.resetPassword("patient@example.com", "123456", "Newpass@1");
        assertThat(otp.getUsed()).isTrue();
        assertThat(user.getPassword()).isEqualTo("encoded");
    }

    @Test
    void validateTokenReturnsFalseForBlankInvalidOrInactiveToken() {
        assertThat(service.validateToken(" ")).isFalse();

        when(jwtService.extractUsername("bad")).thenThrow(new RuntimeException("bad"));
        assertThat(service.validateToken("bad")).isFalse();

        User user = user(1L, "patient@example.com");
        user.setIsActive(false);
        when(jwtService.extractUsername("inactive")).thenReturn("patient@example.com");
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user));
        assertThat(service.validateToken("inactive")).isFalse();
    }

    @Test
    void passwordOtpRejectsInactiveMissingExpiredInvalidAndEmailFailureCases() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.requestPasswordOtp("missing@example.com"))
                .hasMessageContaining("No account");

        User inactive = user(2L, "inactive@example.com");
        inactive.setIsActive(false);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.requestPasswordOtp("inactive@example.com"))
                .hasMessageContaining("deactivated");

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(user(1L, "patient@example.com")));
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed-otp");
        doThrow(new RuntimeException("mail down")).when(notificationClient).sendEmail(any());
        assertThatThrownBy(() -> service.requestPasswordOtp("patient@example.com"))
                .hasMessageContaining("email could not be sent");

        PasswordResetOtp expired = PasswordResetOtp.builder()
                .email("patient@example.com")
                .otpHash("hash")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .verified(false)
                .used(false)
                .build();
        when(otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("patient@example.com"))
                .thenReturn(Optional.of(expired));
        assertThatThrownBy(() -> service.verifyPasswordOtp("patient@example.com", "123456"))
                .hasMessageContaining("expired");

        PasswordResetOtp active = PasswordResetOtp.builder()
                .email("patient@example.com")
                .otpHash("hash")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .used(false)
                .build();
        when(otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("patient@example.com"))
                .thenReturn(Optional.of(active));
        when(passwordEncoder.matches("000000", "hash")).thenReturn(false);
        assertThatThrownBy(() -> service.verifyPasswordOtp("patient@example.com", "000000"))
                .hasMessageContaining("Invalid OTP");

        when(otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc("none@example.com"))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.verifyPasswordOtp("none@example.com", "123456"))
                .hasMessageContaining("OTP not found");
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setName("Patient");
        user.setEmail(email);
        user.setPassword("password");
        user.setPhone("9876543210");
        user.setRole("PATIENT");
        user.setIsActive(true);
        return user;
    }
}
