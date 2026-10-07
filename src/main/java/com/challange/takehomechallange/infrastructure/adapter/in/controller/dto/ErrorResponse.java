package com.challange.takehomechallange.infrastructure.adapter.in.controller.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(int status, String error, String message,
                            List<String> details, Instant timestamp) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, List.of(), Instant.now());
    }

    public static ErrorResponse of(int status, String error, String message, List<String> details) {
        return new ErrorResponse(status, error, message, details, Instant.now());
    }
}