package com.challange.takehomechallange.domain.exception;

import java.util.UUID;

public class NotificationNotFoundException extends DomainException {
    public NotificationNotFoundException(UUID id) {
        super("No se encontró la notificación con id: " + id);
    }
}