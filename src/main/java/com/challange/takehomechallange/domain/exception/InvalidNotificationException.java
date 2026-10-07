package com.challange.takehomechallange.domain.exception;

// Para cualquier dato que el canal rechace (destinatario mal formado, SMS muy largo, token invalido)
public class InvalidNotificationException extends DomainException {
    public InvalidNotificationException(String message) {
        super(message);
    }
}