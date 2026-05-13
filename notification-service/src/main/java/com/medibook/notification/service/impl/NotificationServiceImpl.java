package com.medibook.notification.service.impl;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medibook.notification.dto.BulkNotificationRequestDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.dto.NotificationResponseDTO;
import com.medibook.notification.entity.Notification;
import com.medibook.notification.exception.ResourceNotFoundException;
import com.medibook.notification.repository.NotificationRepository;
import com.medibook.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final JavaMailSender mailSender;

    @Override
    public NotificationResponseDTO send(NotificationRequestDTO request) {
        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .channel(request.getChannel().toUpperCase())
                .relatedId(request.getRelatedId())
                .relatedType(request.getRelatedType())
                .isRead(false)
                .build();

        Notification saved = repository.save(notification);

        if ("EMAIL".equalsIgnoreCase(saved.getChannel())) {
            log.info("EMAIL channel selected. Use /notifications/email to send to actual email.");
        }

        if ("SMS".equalsIgnoreCase(saved.getChannel())) {
            log.info("SMS channel selected. SMS provider can be integrated here.");
        }

        return mapToDTO(saved);
    }

    @Override
    public void sendBulk(List<Long> recipients, String title, String message) {
        List<Notification> notifications = recipients.stream()
                .map(id -> Notification.builder()
                        .recipientId(id)
                        .type("BULK")
                        .title(title)
                        .message(message)
                        .channel("APP")
                        .isRead(false)
                        .build())
                .toList();

        repository.saveAll(notifications);
    }

    @Override
    public List<NotificationResponseDTO> sendBulkAdvanced(BulkNotificationRequestDTO request) {
        List<Notification> notifications = request.getRecipients().stream()
                .map(id -> Notification.builder()
                        .recipientId(id)
                        .type(request.getType() != null ? request.getType() : "BULK")
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .channel(request.getChannel() != null ? request.getChannel().toUpperCase() : "APP")
                        .relatedId(request.getRelatedId())
                        .relatedType(request.getRelatedType())
                        .isRead(false)
                        .build())
                .toList();

        return repository.saveAll(notifications)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setIsRead(true);
        repository.save(notification);
    }

    @Override
    public void markAllRead(Long recipientId) {
        List<Notification> notifications = repository.findByRecipientId(recipientId);

        notifications.forEach(notification -> notification.setIsRead(true));

        repository.saveAll(notifications);
    }

    @Override
    public List<NotificationResponseDTO> getByRecipient(Long recipientId) {
        return repository.findByRecipientId(recipientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public int getUnreadCount(Long recipientId) {
        return repository.countByRecipientIdAndIsRead(recipientId, false);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        repository.deleteByNotificationId(id);
    }

    @Override
    public void sendEmail(String email, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(message);

        mailSender.send(mail);

        log.info("Email sent to {}", email);
    }

    @Override
    public void sendSMS(String phone, String message) {
        log.info("SMS requested for {}. SMS provider is not configured, so no external SMS was sent.", phone);
    }
    @Override
    public List<NotificationResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private NotificationResponseDTO mapToDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .recipientId(notification.getRecipientId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .channel(notification.getChannel())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .isRead(notification.getIsRead())
                .sentAt(notification.getSentAt())
                .build();
    }
}
