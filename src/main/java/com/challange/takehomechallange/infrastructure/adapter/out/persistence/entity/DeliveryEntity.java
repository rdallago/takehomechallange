package com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.DeliveryStatus;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_deliveries")
public class DeliveryEntity {

    @Id
    private UUID id;

    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected DeliveryEntity() {}

    public static DeliveryEntity fromDomain(NotificationDelivery d) {
        DeliveryEntity e = new DeliveryEntity();
        e.id = d.getId();
        e.notificationId = d.getNotificationId();
        e.channel = d.getChannel();
        e.status = d.getStatus();
        e.detail = d.getDetail();
        e.sentAt = d.getSentAt();
        return e;
    }

    public NotificationDelivery toDomain() {
        return new NotificationDelivery(id, notificationId, channel, status, detail, sentAt);
    }
}