package com.medibook.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medibook.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    List<Notification> findByRecipientIdAndIsRead(Long recipientId, Boolean isRead);

    int countByRecipientIdAndIsRead(Long recipientId, Boolean isRead);

    List<Notification> findByType(String type);

    List<Notification> findByRelatedId(Long relatedId);

    void deleteByNotificationId(Long notificationId);
}