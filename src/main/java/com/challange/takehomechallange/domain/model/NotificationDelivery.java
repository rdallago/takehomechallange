package com.challange.takehomechallange.domain.model;

import java.time.Instant;
import java.util.UUID;

public class NotificationDelivery {

    private final UUID id;
    private final UUID notificationId;
    private final Channel channel;
    private final DeliveryStatus status;
    private final String detail; // template, payload, telefono, motivo del error, etc.
    private final Instant sentAt;

    public NotificationDelivery(UUID id, UUID notificationId, Channel channel,
                                DeliveryStatus status, String detail, Instant sentAt) {
        this.id = id;
        this.notificationId = notificationId;
        this.channel = channel;
        this.status = status;
        this.detail = detail;
        this.sentAt = sentAt;
    }

    public static NotificationDelivery sent(UUID notificationId, Channel channel, String detail) {
        return new NotificationDelivery(UUID.randomUUID(), notificationId, channel,
                DeliveryStatus.SENT, detail, Instant.now());
    }

    public static NotificationDelivery failed(UUID notificationId, Channel channel, String reason) {
        return new NotificationDelivery(UUID.randomUUID(), notificationId, channel,
                DeliveryStatus.FAILED, reason, Instant.now());
    }

    public UUID getId() { return id; }
    public UUID getNotificationId() { return notificationId; }
    public Channel getChannel() { return channel; }
    public DeliveryStatus getStatus() { return status; }
    public String getDetail() { return detail; }
    public Instant getSentAt() { return sentAt; }
}