package com.challange.takehomechallange.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Email o contraseña incorrectos");
    }
}