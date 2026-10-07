package com.challange.takehomechallange.domain.port.in;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import java.util.UUID;

public interface CreateNotificationUseCase {

    Notification create(Command command);

    record Command(UUID userId, String title, String content, Channel channel, String recipient) {}
}