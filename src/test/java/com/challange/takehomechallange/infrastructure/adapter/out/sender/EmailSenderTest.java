package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.DeliveryStatus;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmailSenderTest {

    private final EmailSender sender = new EmailSender();

    private Notification emailTo(String recipient) {
        return Notification.create(UUID.randomUUID(), "Hola", "Contenido", Channel.EMAIL, recipient);
    }

    @Test
    void acceptsValidEmail() {
        assertDoesNotThrow(() -> sender.validate(emailTo("alguien@mail.com")));
    }

    @Test
    void rejectsMalformedEmail() {
        assertThrows(InvalidNotificationException.class, () -> sender.validate(emailTo("no-es-un-mail")));
    }

    @Test
    void sendRegistersDeliveryWithTemplate() {
        NotificationDelivery delivery = sender.send(emailTo("alguien@mail.com"));

        assertEquals(DeliveryStatus.SENT, delivery.getStatus());
        assertEquals(Channel.EMAIL, delivery.getChannel());
        assertTrue(delivery.getDetail().contains("<h2>Hola</h2>"));
    }
}