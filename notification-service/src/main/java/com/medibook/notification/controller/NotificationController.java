package com.medibook.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medibook.notification.dto.BulkNotificationRequestDTO;
import com.medibook.notification.dto.EmailRequestDTO;
import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.dto.NotificationResponseDTO;
import com.medibook.notification.dto.SmsRequestDTO;
import com.medibook.notification.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> send(@Valid @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.ok(service.send(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<NotificationResponseDTO>> sendBulk(
            @Valid @RequestBody BulkNotificationRequestDTO request) {
        return ResponseEntity.ok(service.sendBulkAdvanced(request));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }

    @PutMapping("/recipient/{recipientId}/read-all")
    public ResponseEntity<String> markAllRead(@PathVariable Long recipientId) {
        service.markAllRead(recipientId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByRecipient(@PathVariable Long recipientId) {
        return ResponseEntity.ok(service.getByRecipient(recipientId));
    }

    @GetMapping("/recipient/{recipientId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long recipientId) {
        return ResponseEntity.ok(service.getUnreadCount(recipientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        service.sendEmail(request.getEmail(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/sms")
    public ResponseEntity<String> sendSMS(@Valid @RequestBody SmsRequestDTO request) {
        service.sendSMS(request.getPhone(), request.getMessage());
        return ResponseEntity.ok("SMS sent successfully");
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}