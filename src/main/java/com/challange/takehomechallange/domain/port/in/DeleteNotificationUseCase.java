package com.challange.takehomechallange.domain.port.in;

import java.util.UUID;

public interface DeleteNotificationUseCase {

    void delete(UUID notificationId, UUID userId);
}