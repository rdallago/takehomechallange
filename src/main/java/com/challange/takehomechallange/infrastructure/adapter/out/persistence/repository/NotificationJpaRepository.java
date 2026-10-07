package com.challange.takehomechallange.infrastructure.adapter.out.persistence.repository;

import com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    Optional<NotificationEntity> findByIdAndUserId(UUID id, UUID userId);
    List<NotificationEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}