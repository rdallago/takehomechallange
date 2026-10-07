package com.challange.takehomechallange.domain.port.in;

public interface LoginUseCase {

    // Devuelve el token de acceso
    String login(Command command);

    record Command(String email, String password) {}
}