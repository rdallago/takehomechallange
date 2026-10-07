package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SmsSenderTest {

    private final SmsSender sender = new SmsSender();

    @Test
    void acceptsValidSms() {
        Notification n = Notification.create(UUID.randomUUID(), "Hola", "Mensaje corto", Channel.SMS, "+5493794123456");
        assertDoesNotThrow(() -> sender.validate(n));
    }

    @Test
    void rejectsSmsOver160Chars() {
        String longText = "a".repeat(161);
        Notification n = Notification.create(UUID.randomUUID(), "Hola", longText, Channel.SMS, "+5493794123456");
        assertThrows(InvalidNotificationException.class, () -> sender.validate(n));
    }
}