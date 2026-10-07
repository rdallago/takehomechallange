package com.challange.takehomechallange.domain.port.in;

import com.challange.takehomechallange.domain.model.User;

public interface RegisterUserUseCase {

    User register(Command command);

    record Command(String email, String password) {}
}