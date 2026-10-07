package com.challange.takehomechallange.infrastructure.adapter.in.controller;

import com.challange.takehomechallange.domain.port.in.LoginUseCase;
import com.challange.takehomechallange.domain.port.in.RegisterUserUseCase;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.LoginRequest;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.RegisterRequest;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.TokenResponse;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final LoginUseCase login;

    public AuthController(RegisterUserUseCase registerUser, LoginUseCase login) {
        this.registerUser = registerUser;
        this.login = login;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un usuario nuevo")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        var user = registerUser.register(
                new RegisterUserUseCase.Command(request.email(), request.password()));
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion y obtener el token de acceso")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        String token = login.login(new LoginUseCase.Command(request.email(), request.password()));
        return TokenResponse.bearer(token);
    }
}