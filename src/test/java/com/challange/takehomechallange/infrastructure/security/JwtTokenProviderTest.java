package com.challange.takehomechallange.infrastructure.security;

import com.challange.takehomechallange.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String SECRET = "un-secret-de-test-bastante-largo-para-hs256-0123456789";

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60);

    @Test
    void validTokenReturnsUserId() {
        User user = User.create("test@mail.com", "hash");
        String token = provider.generateToken(user);

        Optional<UUID> result = provider.validateAndGetUserId(token);

        assertEquals(user.getId(), result.orElseThrow());
    }

    @Test
    void garbageTokenIsRejected() {
        assertTrue(provider.validateAndGetUserId("esto.no.es.un.jwt").isEmpty());
    }

    @Test
    void expiredTokenIsRejected() {
        // expiracion de 0 minutos: nace vencido
        JwtTokenProvider expired = new JwtTokenProvider(SECRET, 0);
        String token = expired.generateToken(User.create("test@mail.com", "hash"));

        assertTrue(provider.validateAndGetUserId(token).isEmpty());
    }

    @Test
    void tokenSignedWithOtherSecretIsRejected() {
        JwtTokenProvider other = new JwtTokenProvider("otro-secret-distinto-igual-de-largo-9876543210-abcdef", 60);
        String token = other.generateToken(User.create("test@mail.com", "hash"));

        assertTrue(provider.validateAndGetUserId(token).isEmpty());
    }
}