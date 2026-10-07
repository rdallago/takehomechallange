package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.InvalidCredentialsException;
import com.challange.takehomechallange.domain.model.User;
import com.challange.takehomechallange.domain.port.in.LoginUseCase;
import com.challange.takehomechallange.domain.port.out.PasswordEncoderPort;
import com.challange.takehomechallange.domain.port.out.TokenProviderPort;
import com.challange.takehomechallange.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock UserRepositoryPort userRepository;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock TokenProviderPort tokenProvider;
    @InjectMocks LoginService service;

    private final User user = User.create("juan@mail.com", "HASH");

    @Test
    void returnsTokenWithValidCredentials() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("clave12345", "HASH")).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn("TOKEN");

        String token = service.login(new LoginUseCase.Command("Juan@mail.com", "clave12345"));

        assertEquals("TOKEN", token);
    }

    @Test
    void failsWhenUserDoesNotExist() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginUseCase.Command("juan@mail.com", "x")));

        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    void failsWhenPasswordIsWrong() {
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mala", "HASH")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginUseCase.Command("juan@mail.com", "mala")));

        verify(tokenProvider, never()).generateToken(any());
    }
}