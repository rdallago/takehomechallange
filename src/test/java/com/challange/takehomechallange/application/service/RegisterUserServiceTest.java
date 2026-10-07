package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.EmailAlreadyExistsException;
import com.challange.takehomechallange.domain.model.User;
import com.challange.takehomechallange.domain.port.in.RegisterUserUseCase;
import com.challange.takehomechallange.domain.port.out.PasswordEncoderPort;
import com.challange.takehomechallange.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock UserRepositoryPort userRepository;
    @Mock PasswordEncoderPort passwordEncoder;
    @InjectMocks RegisterUserService service;

    @Test
    void registersUserWithHashedPasswordAndNormalizedEmail() {
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("clave12345")).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.register(new RegisterUserUseCase.Command("  Juan@Mail.com ", "clave12345"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("juan@mail.com", captor.getValue().getEmail());
        assertEquals("HASH", captor.getValue().getPasswordHash());
    }

    @Test
    void failsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> service.register(new RegisterUserUseCase.Command("juan@mail.com", "clave12345")));

        verify(userRepository, never()).save(any());
    }
}