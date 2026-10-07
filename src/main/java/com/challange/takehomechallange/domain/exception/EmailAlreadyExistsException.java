package com.challange.takehomechallange.domain.exception;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("Ya existe un usuario registrado con el email: " + email);
    }
}