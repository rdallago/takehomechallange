package com.challange.takehomechallange.domain.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash; // siempre hasheada, nunca la clave en texto plano
    private final Instant createdAt;

    // Constructor completo, lo usa el adapter de persistencia para reconstruir desde la BD
    public User(UUID id, String email, String passwordHash, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    // Fabrica para usuarios nuevos
    public static User create(String email, String passwordHash) {
        return new User(UUID.randomUUID(), email.trim().toLowerCase(), passwordHash, Instant.now());
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }//campo de auditoría; no afecta la lógica, pero permite trazabilidad y futuras funcionalidades sin migrar datos.
}