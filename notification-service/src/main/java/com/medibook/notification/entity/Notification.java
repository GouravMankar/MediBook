package com.medibook.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private String type; 

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private String channel; 

    private Long relatedId;
    private String relatedType;

    private Boolean isRead;

    private LocalDateTime sentAt;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }
}