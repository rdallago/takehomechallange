package com.challange.takehomechallange.domain.port.out;

import com.challange.takehomechallange.domain.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    // Buscar siempre por id + dueño evita que alguien toque notificaciones ajenas
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
    List<Notification> findAllByUserId(UUID userId);
    void delete(Notification notification);
}