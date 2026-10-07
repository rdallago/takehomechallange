package com.challange.takehomechallange.infrastructure.adapter.in.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}