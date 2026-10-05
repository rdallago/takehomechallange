package com.challange.takehomechallange.infrastructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Endpoints de verificación y diagnóstico del sistema")
public class HealthController {

    @GetMapping
    @Operation(
        summary = "Prueba de conectividad y estado del Backend", 
        description = "Retorna un mensaje de confirmación y el estado general del entorno."
    )
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "¡Hola Mundo! El entorno Spring Boot, Docker, PostgreSQL y Swagger está 100% operativo.",
            "timestamp", LocalDateTime.now().toString(),
            "architecture", "Clean Architecture / Hexagonal"
        ));
    }
}