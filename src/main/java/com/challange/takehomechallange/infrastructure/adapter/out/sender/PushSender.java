package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class PushSender implements NotificationSenderPort {

    private static final Logger log = LoggerFactory.getLogger(PushSender.class);

    // Token tipo FCM: letras, numeros y algunos simbolos, largo minimo 20
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9:_\\-]{20,}$");

    private final ObjectMapper objectMapper;

    public PushSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Channel channel() {
        return Channel.PUSH;
    }

    @Override
    public void validate(Notification notification) {
        String token = notification.getRecipient();
        if (token == null || !TOKEN_PATTERN.matcher(token).matches()) {
            throw new InvalidNotificationException(
                    "El token de dispositivo no es valido (minimo 20 caracteres alfanumericos)");
        }
    }

    @Override
    public NotificationDelivery send(Notification notification) {
        // Paso 1: armar el payload
        String payload;
        try {
            payload = buildPayload(notification);
        } catch (JsonProcessingException e) {
            // Unico caso donde registramos FAILED: no se pudo armar el payload
            log.error("[PUSH] Error armando payload", e);
            return NotificationDelivery.failed(notification.getId(), Channel.PUSH,
                    "Error al formatear el payload: " + e.getMessage());
        }

        // Paso 2: "enviar" (simulado)
        log.info("[PUSH] Enviando al dispositivo {}", notification.getRecipient());

        // Paso 3: registrar estado
        String detail = "payload=" + payload;
        return NotificationDelivery.sent(notification.getId(), Channel.PUSH, detail);
    }

    private String buildPayload(Notification n) throws JsonProcessingException {
        Map<String, Object> notificationPart = new LinkedHashMap<>();
        notificationPart.put("title", n.getTitle());
        notificationPart.put("body", n.getContent());

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("to", n.getRecipient());
        root.put("notification", notificationPart);

        return objectMapper.writeValueAsString(root);
    }
}