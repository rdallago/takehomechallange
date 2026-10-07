package com.challange.takehomechallange.infrastructure.adapter.in.controller.dto;

import com.challange.takehomechallange.domain.model.User;

import java.time.Instant;
import java.util.UUID;

// Nunca devolvemos el hash de la clave
public record UserResponse(UUID id, String email, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}