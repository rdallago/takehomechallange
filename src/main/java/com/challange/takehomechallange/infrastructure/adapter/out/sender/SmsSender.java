package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Pattern;

@Component
public class SmsSender implements NotificationSenderPort {

    private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

    private static final int MAX_LENGTH = 160;

    // Numero con + opcional y entre 8 y 15 digitos
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{8,15}$");

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public void validate(Notification notification) {
        String recipient = notification.getRecipient();
        if (recipient == null || !PHONE_PATTERN.matcher(recipient).matches()) {
            throw new InvalidNotificationException("El numero de telefono no es valido: " + recipient);
        }
        String content = notification.getContent();
        if (content == null || content.length() > MAX_LENGTH) {
            throw new InvalidNotificationException(
                    "El contenido del SMS no puede superar los " + MAX_LENGTH + " caracteres");
        }
    }

    @Override
    public NotificationDelivery send(Notification notification) {
        Instant now = Instant.now();

        log.info("[SMS] Enviando a {} ({} caracteres)", notification.getRecipient(), notification.getContent().length());

        // Se registra numero y fecha de envio
        String detail = "number=" + notification.getRecipient()
                + " | sentAt=" + now
                + " | length=" + notification.getContent().length();
        return NotificationDelivery.sent(notification.getId(), Channel.SMS, detail);
    }
}