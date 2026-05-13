package com.medibook.notification.service;

import java.util.List;

import com.medibook.notification.dto.BulkNotificationRequestDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.dto.NotificationResponseDTO;

public interface NotificationService {

    NotificationResponseDTO send(NotificationRequestDTO request);

    void sendBulk(List<Long> recipients, String title, String message);

    void markAsRead(Long id);

    void markAllRead(Long recipientId);

    List<NotificationResponseDTO> getByRecipient(Long recipientId);

    int getUnreadCount(Long recipientId);

    void deleteNotification(Long id);

    void sendEmail(String email, String subject, String message);

    void sendSMS(String phone, String message);

    List<NotificationResponseDTO> getAll();

    List<NotificationResponseDTO> sendBulkAdvanced(BulkNotificationRequestDTO request);
}