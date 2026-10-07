package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.NotificationNotFoundException;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.in.UpdateNotificationUseCase;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateNotificationService implements UpdateNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationSenderResolver senderResolver;

    public UpdateNotificationService(NotificationRepositoryPort notificationRepository,
                                     NotificationSenderResolver senderResolver) {
        this.notificationRepository = notificationRepository;
        this.senderResolver = senderResolver;
    }

    @Override
    @Transactional
    public Notification update(Command command) {
        // Si no existe o es de otro usuario, devolvemos 404 en ambos casos
        Notification notification = notificationRepository
                .findByIdAndUserId(command.notificationId(), command.userId())
                .orElseThrow(() -> new NotificationNotFoundException(command.notificationId()));

        notification.update(command.title(), command.content(), command.channel(), command.recipient());

        // Los nuevos datos tambien tienen que cumplir las reglas del canal
        senderResolver.resolve(notification.getChannel()).validate(notification);

        // Editar no reenvia: el envio ocurre solo al crear
        return notificationRepository.save(notification);
    }
}