package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.EmailAlreadyExistsException;
import com.challange.takehomechallange.domain.model.User;
import com.challange.takehomechallange.domain.port.in.RegisterUserUseCase;
import com.challange.takehomechallange.domain.port.out.PasswordEncoderPort;
import com.challange.takehomechallange.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(Command command) {
        // Mismo criterio de normalizacion que User.create
        String email = command.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String hash = passwordEncoder.encode(command.password());
        return userRepository.save(User.create(email, hash));
    }
}