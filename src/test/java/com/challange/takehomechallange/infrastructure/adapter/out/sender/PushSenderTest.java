package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.DeliveryStatus;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PushSenderTest {

    private final PushSender sender = new PushSender(new ObjectMapper());

    private Notification pushTo(String token) {
        return Notification.create(UUID.randomUUID(), "Titulo", "Cuerpo", Channel.PUSH, token);
    }

    @Test
    void acceptsValidDeviceToken() {
        assertDoesNotThrow(() -> sender.validate(pushTo("abcdefghij1234567890xyz")));
    }

    @Test
    void rejectsShortToken() {
        assertThrows(InvalidNotificationException.class, () -> sender.validate(pushTo("corto")));
    }

    @Test
    void sendBuildsPayloadAndRegistersStatus() {
        NotificationDelivery delivery = sender.send(pushTo("abcdefghij1234567890xyz"));

        assertEquals(DeliveryStatus.SENT, delivery.getStatus());
        assertTrue(delivery.getDetail().contains("\"title\":\"Titulo\""));
        assertTrue(delivery.getDetail().contains("\"to\":\"abcdefghij1234567890xyz\""));
    }
}