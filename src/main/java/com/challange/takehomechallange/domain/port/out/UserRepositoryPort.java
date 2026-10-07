package com.challange.takehomechallange.domain.port.out;

import com.challange.takehomechallange.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}