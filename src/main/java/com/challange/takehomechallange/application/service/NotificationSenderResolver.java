package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationSenderResolver {

    private final Map<Channel, NotificationSenderPort> senders = new EnumMap<>(Channel.class);

    public NotificationSenderResolver(List<NotificationSenderPort> availableSenders) {
        for (NotificationSenderPort sender : availableSenders) {
            senders.put(sender.channel(), sender);
        }
    }

    public NotificationSenderPort resolve(Channel channel) {
        NotificationSenderPort sender = senders.get(channel);
        if (sender == null) {
            // Esto pasaria si alguien agrega un valor al enum y se olvida del sender
            throw new IllegalStateException("No hay sender configurado para el canal " + channel);
        }
        return sender;
    }
}