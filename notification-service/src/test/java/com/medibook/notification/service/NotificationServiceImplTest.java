package com.medibook.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.medibook.notification.dto.NotificationRequestDTO;
import com.medibook.notification.dto.NotificationResponseDTO;
import com.medibook.notification.entity.Notification;
import com.medibook.notification.exception.ResourceNotFoundException;
import com.medibook.notification.repository.NotificationRepository;
import com.medibook.notification.service.impl.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void sendStoresUnreadNotification() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setRecipientId(1L);
        request.setType("OTP");
        request.setTitle("Password Reset");
        request.setMessage("Your OTP is 123456");
        request.setChannel("app");

        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setNotificationId(9L);
            return notification;
        });

        NotificationResponseDTO response = service.send(request);

        assertThat(response.getNotificationId()).isEqualTo(9L);
        assertThat(response.getChannel()).isEqualTo("APP");
        assertThat(response.getIsRead()).isFalse();
    }

    @Test
    void sendEmailDelegatesToJavaMailSender() {
        service.sendEmail("patient@example.com", "OTP", "Use 123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getTo()).containsExactly("patient@example.com");
        assertThat(captor.getValue().getSubject()).isEqualTo("OTP");
        assertThat(captor.getValue().getText()).contains("123456");
    }

    @Test
    void getUnreadCountUsesRepository() {
        when(repository.countByRecipientIdAndIsRead(4L, false)).thenReturn(2);

        assertThat(service.getUnreadCount(4L)).isEqualTo(2);
    }

    @Test
    void bulkAndAdvancedBulkStoreNotificationsForRecipients() {
        service.sendBulk(List.of(1L, 2L), "Title", "Message");
        verify(repository).saveAll(any());

        com.medibook.notification.dto.BulkNotificationRequestDTO request =
                new com.medibook.notification.dto.BulkNotificationRequestDTO();
        request.setRecipients(List.of(3L, 4L));
        request.setType("ADMIN");
        request.setTitle("Hello");
        request.setMessage("World");
        request.setChannel("app");
        when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<NotificationResponseDTO> response = service.sendBulkAdvanced(request);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getChannel()).isEqualTo("APP");
    }

    @Test
    void readDeleteAndQueryMethodsUseRepository() {
        Notification notification = notification(10L, false);
        when(repository.findById(10L)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);

        service.markAsRead(10L);
        assertThat(notification.getIsRead()).isTrue();

        Notification unread = notification(11L, false);
        when(repository.findByRecipientId(1L)).thenReturn(List.of(unread));
        service.markAllRead(1L);
        assertThat(unread.getIsRead()).isTrue();
        verify(repository).saveAll(List.of(unread));

        when(repository.findByRecipientId(2L)).thenReturn(List.of(notification));
        when(repository.findAll()).thenReturn(List.of(notification));
        assertThat(service.getByRecipient(2L)).hasSize(1);
        assertThat(service.getAll()).hasSize(1);

        when(repository.existsById(10L)).thenReturn(true);
        service.deleteNotification(10L);
        verify(repository).deleteByNotificationId(10L);
    }

    @Test
    void markReadAndDeleteRejectMissingNotifications() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.markAsRead(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        when(repository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteNotification(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void sendSmsIsNoopWhenProviderIsNotConfigured() {
        service.sendSMS("9999999999", "Hello");
    }

    private Notification notification(Long id, boolean read) {
        return Notification.builder()
                .notificationId(id)
                .recipientId(1L)
                .type("APP")
                .title("Title")
                .message("Message")
                .channel("APP")
                .isRead(read)
                .build();
    }
}
