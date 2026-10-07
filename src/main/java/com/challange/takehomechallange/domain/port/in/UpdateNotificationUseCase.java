package com.challange.takehomechallange.domain.port.in;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import java.util.UUID;

public interface UpdateNotificationUseCase {

    Notification update(Command command);

    record Command(UUID notificationId, UUID userId, String title, String content,
                   Channel channel, String recipient) {}
}