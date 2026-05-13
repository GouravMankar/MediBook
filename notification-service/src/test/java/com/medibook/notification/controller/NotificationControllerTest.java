package com.medibook.notification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.medibook.notification.dto.BulkNotificationRequestDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.dto.NotificationResponseDTO;
import com.medibook.notification.service.NotificationService;

@WebMvcTest(controllers = NotificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    @Test
    void sendReturnsNotification() throws Exception {
        when(service.send(any(NotificationRequestDTO.class))).thenReturn(notification());

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientId": 1,
                                  "type": "OTP",
                                  "title": "Password Reset",
                                  "message": "Your OTP is 123456",
                                  "channel": "APP"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Password Reset"));
    }

    @Test
    void getUnreadCountReturnsCount() throws Exception {
        when(service.getUnreadCount(1L)).thenReturn(3);

        mockMvc.perform(get("/notifications/recipient/1/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    void sendBulkReturnsNotifications() throws Exception {
        when(service.sendBulkAdvanced(any(BulkNotificationRequestDTO.class))).thenReturn(List.of(notification()));

        mockMvc.perform(post("/notifications/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipients": [1, 2],
                                  "title": "Reminder",
                                  "message": "Appointment soon",
                                  "type": "REMINDER",
                                  "channel": "APP"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1));
    }

    @Test
    void sendEmailReturnsSuccessMessage() throws Exception {
        doNothing().when(service).sendEmail("patient@example.com", "OTP", "Use 123456");

        mockMvc.perform(post("/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "patient@example.com",
                                  "subject": "OTP",
                                  "message": "Use 123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully"));
    }

    @Test
    void markAsReadReturnsMessage() throws Exception {
        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as read"));
    }

    @Test
    void markAllReadReturnsMessage() throws Exception {
        mockMvc.perform(put("/notifications/recipient/1/read-all"))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications marked as read"));

        verify(service).markAllRead(1L);
    }

    @Test
    void getByRecipientReturnsNotifications() throws Exception {
        when(service.getByRecipient(1L)).thenReturn(List.of(notification()));

        mockMvc.perform(get("/notifications/recipient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientId").value(1));
    }

    @Test
    void deleteNotificationReturnsMessage() throws Exception {
        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification deleted successfully"));

        verify(service).deleteNotification(1L);
    }

    @Test
    void sendSmsReturnsSuccessMessage() throws Exception {
        mockMvc.perform(post("/notifications/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "9876543210",
                                  "message": "Use 123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("SMS sent successfully"));

        verify(service).sendSMS("9876543210", "Use 123456");
    }

    @Test
    void getAllReturnsNotifications() throws Exception {
        when(service.getAll()).thenReturn(List.of(notification()));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Password Reset"));
    }

    private NotificationResponseDTO notification() {
        return NotificationResponseDTO.builder()
                .notificationId(1L)
                .recipientId(1L)
                .type("OTP")
                .title("Password Reset")
                .message("Your OTP is 123456")
                .channel("APP")
                .isRead(false)
                .build();
    }
}
