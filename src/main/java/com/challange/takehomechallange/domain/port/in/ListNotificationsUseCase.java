package com.challange.takehomechallange.domain.port.in;

import com.challange.takehomechallange.domain.model.Notification;
import java.util.List;
import java.util.UUID;

public interface ListNotificationsUseCase {

    List<Notification> listByUser(UUID userId);
}