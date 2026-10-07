package com.challange.takehomechallange.infrastructure.adapter.out.persistence.repository;

import com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryEntity, UUID> {
    List<DeliveryEntity> findByNotificationId(UUID notificationId);
}