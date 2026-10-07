package com.challange.takehomechallange.domain.port.out;

import com.challange.takehomechallange.domain.model.User;

public interface TokenProviderPort {
    String generateToken(User user);
}