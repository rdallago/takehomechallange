package com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity;

import com.challange.takehomechallange.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // JPA exige un constructor vacio
    protected UserEntity() {}

    public static UserEntity fromDomain(User user) {
        UserEntity e = new UserEntity();
        e.id = user.getId();
        e.email = user.getEmail();
        e.passwordHash = user.getPasswordHash();
        e.createdAt = user.getCreatedAt();
        return e;
    }

    public User toDomain() {
        return new User(id, email, passwordHash, createdAt);
    }
}