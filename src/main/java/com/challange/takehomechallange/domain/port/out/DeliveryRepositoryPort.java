package com.challange.takehomechallange.domain.port.out;

import com.challange.takehomechallange.domain.model.NotificationDelivery;
import java.util.List;
import java.util.UUID;

public interface DeliveryRepositoryPort {
    NotificationDelivery save(NotificationDelivery delivery);
    List<NotificationDelivery> findByNotificationId(UUID notificationId);
}