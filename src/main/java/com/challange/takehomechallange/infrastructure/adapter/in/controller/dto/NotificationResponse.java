package com.challange.takehomechallange.infrastructure.adapter.in.controller.dto;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String title, String content, Channel channel,
                                   String recipient, Instant createdAt, Instant updatedAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getTitle(), n.getContent(), n.getChannel(),
                n.getRecipient(), n.getCreatedAt(), n.getUpdatedAt());
    }
}