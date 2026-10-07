package com.challange.takehomechallange.infrastructure.adapter.in.controller.dto;

import com.challange.takehomechallange.domain.model.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Se usa para crear y para modificar
public record NotificationRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank String content,
        @NotNull Channel channel,
        // email, telefono o device token segun el canal (lo valida el sender)
        @NotBlank @Size(max = 255) String recipient) {
}