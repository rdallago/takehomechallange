package com.challange.takehomechallange.infrastructure.adapter.out.persistence.adapter;

import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.repository.NotificationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository repository;

    public NotificationPersistenceAdapter(NotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        return repository.save(NotificationEntity.fromDomain(notification)).toDomain();
    }

    @Override
    public Optional<Notification> findByIdAndUserId(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(NotificationEntity::toDomain);
    }

    @Override
    public List<Notification> findAllByUserId(UUID userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Notification notification) {
        repository.deleteById(notification.getId());
    }
}