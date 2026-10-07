package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.NotificationNotFoundException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteNotificationServiceTest {

    @Mock NotificationRepositoryPort notificationRepository;
    @InjectMocks DeleteNotificationService service;

    private final UUID userId = UUID.randomUUID();
    private final Notification existing =
            Notification.create(userId, "Hola", "Contenido", Channel.EMAIL, "a@mail.com");

    @Test
    void deletesOwnNotification() {
        when(notificationRepository.findByIdAndUserId(existing.getId(), userId))
                .thenReturn(Optional.of(existing));

        service.delete(existing.getId(), userId);

        verify(notificationRepository).delete(existing);
    }

    @Test
    void failsWhenNotificationBelongsToAnotherUser() {
        UUID otherUser = UUID.randomUUID();
        when(notificationRepository.findByIdAndUserId(existing.getId(), otherUser))
                .thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> service.delete(existing.getId(), otherUser));

        verify(notificationRepository, never()).delete(any());
    }
}