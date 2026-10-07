package com.challange.takehomechallange.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final UUID userId;
    private String title;
    private String content;
    private Channel channel;
    private String recipient; // email, telefono o device token segun el canal
    private final Instant createdAt;
    private Instant updatedAt;

    public Notification(UUID id, UUID userId, String title, String content,
                        Channel channel, String recipient,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.channel = channel;
        this.recipient = recipient;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Notification create(UUID userId, String title, String content,
                                      Channel channel, String recipient) {
        Instant now = Instant.now();
        return new Notification(UUID.randomUUID(), userId, title, content, channel, recipient, now, now);
    }

    // Modificacion de una notificacion existente
    public void update(String title, String content, Channel channel, String recipient) {
        this.title = title;
        this.content = content;
        this.channel = channel;
        this.recipient = recipient;
        this.updatedAt = Instant.now();
    }

    // Para el chequeo de autorizacion: la notificacion es de este usuario?
    public boolean belongsTo(UUID userId) {
        return this.userId.equals(userId);
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Channel getChannel() { return channel; }
    public String getRecipient() { return recipient; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}