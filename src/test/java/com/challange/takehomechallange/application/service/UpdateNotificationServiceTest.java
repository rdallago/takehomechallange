package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.exception.NotificationNotFoundException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.in.UpdateNotificationUseCase;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateNotificationServiceTest {

    @Mock NotificationRepositoryPort notificationRepository;
    @Mock NotificationSenderResolver senderResolver;
    @Mock NotificationSenderPort sender;
    @InjectMocks UpdateNotificationService service;

    private final UUID userId = UUID.randomUUID();
    private final Notification existing =
            Notification.create(userId, "Viejo", "Viejo contenido", Channel.EMAIL, "a@mail.com");

    private UpdateNotificationUseCase.Command command(UUID ownerId) {
        return new UpdateNotificationUseCase.Command(
                existing.getId(), ownerId, "Nuevo", "Nuevo contenido", Channel.EMAIL, "b@mail.com");
    }

    @Test
    void updatesOwnNotification() {
        when(notificationRepository.findByIdAndUserId(existing.getId(), userId))
                .thenReturn(Optional.of(existing));
        when(senderResolver.resolve(Channel.EMAIL)).thenReturn(sender);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification result = service.update(command(userId));

        assertEquals("Nuevo", result.getTitle());
        assertEquals("b@mail.com", result.getRecipient());
        verify(sender).validate(existing);
    }

    @Test
    void failsWhenNotificationBelongsToAnotherUser() {
        UUID otherUser = UUID.randomUUID();
        when(notificationRepository.findByIdAndUserId(existing.getId(), otherUser))
                .thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> service.update(command(otherUser)));

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void doesNotSaveWhenNewDataIsInvalidForChannel() {
        when(notificationRepository.findByIdAndUserId(existing.getId(), userId))
                .thenReturn(Optional.of(existing));
        when(senderResolver.resolve(Channel.EMAIL)).thenReturn(sender);
        doThrow(new InvalidNotificationException("invalido")).when(sender).validate(any());

        assertThrows(InvalidNotificationException.class, () -> service.update(command(userId)));

        verify(notificationRepository, never()).save(any());
    }
}