package com.challange.takehomechallange.infrastructure.adapter.out.persistence.adapter;

import com.challange.takehomechallange.domain.model.User;
import com.challange.takehomechallange.domain.port.out.UserRepositoryPort;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return repository.save(UserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}