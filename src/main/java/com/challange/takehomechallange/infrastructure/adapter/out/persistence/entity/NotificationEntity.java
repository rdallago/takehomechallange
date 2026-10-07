package com.challange.takehomechallange.infrastructure.adapter.out.persistence.entity;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    // Guardo solo el id del usuario (sin @ManyToOne): la FK ya la garantiza la BD
    // y nos ahorramos cargas perezosas innecesarias
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Channel channel;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected NotificationEntity() {}

    public static NotificationEntity fromDomain(Notification n) {
        NotificationEntity e = new NotificationEntity();
        e.id = n.getId();
        e.userId = n.getUserId();
        e.title = n.getTitle();
        e.content = n.getContent();
        e.channel = n.getChannel();
        e.recipient = n.getRecipient();
        e.createdAt = n.getCreatedAt();
        e.updatedAt = n.getUpdatedAt();
        return e;
    }

    public Notification toDomain() {
        return new Notification(id, userId, title, content, channel, recipient, createdAt, updatedAt);
    }
}