package com.medibook.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.auth.dto.LoginResponse;
import com.medibook.auth.dto.AuthResponse;
import com.medibook.auth.entity.User;
import com.medibook.auth.config.JwtAuthenticationFilter;
import com.medibook.auth.service.AuthService;
import com.medibook.auth.service.serviceimpl.JwtService;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Test
    void loginReturnsTokenPayload() throws Exception {
        when(authService.login("patient@example.com", "Password@1"))
                .thenReturn(LoginResponse.builder()
                        .id(1L)
                        .email("patient@example.com")
                        .name("Patient")
                        .role("PATIENT")
                        .token("jwt-token")
                        .build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "patient@example.com",
                                  "password": "Password@1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    @Test
    void registerLogoutRefreshAndProfileUpdateEndpointsDelegateToService() throws Exception {
        when(authService.register(any()))
                .thenReturn(AuthResponse.builder()
                        .token("jwt-token")
                        .tokenType("Bearer")
                        .userId(1L)
                        .name("Patient")
                        .email("patient@example.com")
                        .role("PATIENT")
                        .build());
        doNothing().when(authService).logout("patient@example.com");
        when(authService.refreshToken("old-token")).thenReturn("new-token");

        User updated = new User();
        updated.setId(1L);
        updated.setName("Patient Updated");
        updated.setEmail("patient@example.com");
        updated.setPhone("9876543210");
        updated.setRole("PATIENT");
        updated.setIsActive(true);
        when(authService.updateProfile(any(Long.class), any(User.class))).thenReturn(updated);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Patient",
                                  "email": "patient@example.com",
                                  "password": "Password@1",
                                  "phone": "9876543210",
                                  "role": "PATIENT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        mockMvc.perform(post("/auth/logout").param("email", "patient@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        mockMvc.perform(post("/auth/refresh").param("token", "old-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("new-token"));

        mockMvc.perform(put("/auth/profile/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Patient Updated",
                                  "email": "patient@example.com",
                                  "phone": "9876543210",
                                  "role": "PATIENT",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patient Updated"));
    }

    @Test
    void forgotPasswordRequestReturnsSuccessMessage() throws Exception {
        doNothing().when(authService).requestPasswordOtp("patient@example.com");

        mockMvc.perform(post("/auth/forgot-password/request-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"patient@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent to registered email"));
    }

    @Test
    void passwordOtpResetPasswordChangePasswordAndDeactivateEndpointsReturnMessages() throws Exception {
        doNothing().when(authService).verifyPasswordOtp("patient@example.com", "123456");
        doNothing().when(authService).resetPassword("patient@example.com", "123456", "Newpass@1");
        doNothing().when(authService).changePassword(1L, "Newpass@1");
        doNothing().when(authService).deactivateAccount(1L);

        mockMvc.perform(post("/auth/forgot-password/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "patient@example.com",
                                  "otp": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified"));

        mockMvc.perform(post("/auth/forgot-password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "patient@example.com",
                                  "otp": "123456",
                                  "newPassword": "Newpass@1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        mockMvc.perform(put("/auth/password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"Newpass@1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        mockMvc.perform(put("/auth/deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deactivated successfully"));
    }

    @Test
    void getProfileReturnsUser() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setName("Patient");
        user.setEmail("patient@example.com");
        user.setRole("PATIENT");
        user.setIsActive(true);

        when(authService.getUserById(3L)).thenReturn(user);

        mockMvc.perform(get("/auth/profile/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("patient@example.com"));
    }

    @Test
    void invalidRegisterPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
