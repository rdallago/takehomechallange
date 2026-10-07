package com.challange.takehomechallange.infrastructure.adapter.out.persistence.adapter;

import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.out.DeliveryRepositoryPort;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity.DeliveryEntity;
import com.challange.takehomechallange.infrastructure.adapter.out.persistence.repository.DeliveryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DeliveryPersistenceAdapter implements DeliveryRepositoryPort {

    private final DeliveryJpaRepository repository;

    public DeliveryPersistenceAdapter(DeliveryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public NotificationDelivery save(NotificationDelivery delivery) {
        return repository.save(DeliveryEntity.fromDomain(delivery)).toDomain();
    }

    @Override
    public List<NotificationDelivery> findByNotificationId(UUID notificationId) {
        return repository.findByNotificationId(notificationId).stream()
                .map(DeliveryEntity::toDomain)
                .toList();
    }
}