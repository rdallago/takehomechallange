package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.in.CreateNotificationUseCase;
import com.challange.takehomechallange.domain.port.out.DeliveryRepositoryPort;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNotificationServiceTest {

    @Mock NotificationRepositoryPort notificationRepository;
    @Mock DeliveryRepositoryPort deliveryRepository;
    @Mock NotificationSenderResolver senderResolver;
    @Mock NotificationSenderPort sender;
    @InjectMocks CreateNotificationService service;

    private final UUID userId = UUID.randomUUID();

    private CreateNotificationUseCase.Command command() {
        return new CreateNotificationUseCase.Command(
                userId, "Hola", "Contenido", Channel.EMAIL, "alguien@mail.com");
    }

    @Test
    void validatesSavesSendsAndRegistersDelivery() {
        when(senderResolver.resolve(Channel.EMAIL)).thenReturn(sender);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sender.send(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            return NotificationDelivery.sent(n.getId(), Channel.EMAIL, "ok");
        });

        Notification result = service.create(command());

        assertEquals(userId, result.getUserId());

        // El orden importa: validar -> guardar -> enviar
        InOrder order = inOrder(sender, notificationRepository);
        order.verify(sender).validate(any(Notification.class));
        order.verify(notificationRepository).save(any(Notification.class));
        order.verify(sender).send(any(Notification.class));

        verify(deliveryRepository).save(any(NotificationDelivery.class));
    }

    @Test
    void doesNotSaveNorSendWhenValidationFails() {
        when(senderResolver.resolve(Channel.EMAIL)).thenReturn(sender);
        doThrow(new InvalidNotificationException("email invalido"))
                .when(sender).validate(any(Notification.class));

        assertThrows(InvalidNotificationException.class, () -> service.create(command()));

        verify(notificationRepository, never()).save(any());
        verify(sender, never()).send(any());
        verify(deliveryRepository, never()).save(any());
    }
}