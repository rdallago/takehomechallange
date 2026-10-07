package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.InvalidCredentialsException;
import com.challange.takehomechallange.domain.model.User;
import com.challange.takehomechallange.domain.port.in.LoginUseCase;
import com.challange.takehomechallange.domain.port.out.PasswordEncoderPort;
import com.challange.takehomechallange.domain.port.out.TokenProviderPort;
import com.challange.takehomechallange.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginService(UserRepositoryPort userRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(Command command) {
        String email = command.email().trim().toLowerCase();

        // Mismo error si no existe el usuario o si la clave es incorrecta,
        // asi no se puede averiguar que emails estan registrados
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.generateToken(user);
    }
}