package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.in.CreateNotificationUseCase;
import com.challange.takehomechallange.domain.port.out.DeliveryRepositoryPort;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateNotificationService implements CreateNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final DeliveryRepositoryPort deliveryRepository;
    private final NotificationSenderResolver senderResolver;

    public CreateNotificationService(NotificationRepositoryPort notificationRepository,
                                     DeliveryRepositoryPort deliveryRepository,
                                     NotificationSenderResolver senderResolver) {
        this.notificationRepository = notificationRepository;
        this.deliveryRepository = deliveryRepository;
        this.senderResolver = senderResolver;
    }

    @Override
    @Transactional
    public Notification create(Command command) {
        Notification notification = Notification.create(
                command.userId(), command.title(), command.content(),
                command.channel(), command.recipient());

        // 1. Elegir la estrategia segun el canal
        NotificationSenderPort sender = senderResolver.resolve(command.channel());

        // 2. Validar ANTES de guardar: si es invalida no llega a la BD
        sender.validate(notification);

        // 3. Guardar la notificacion
        Notification saved = notificationRepository.save(notification);

        // 4. Enviar (simulado) y registrar el resultado
        NotificationDelivery delivery = sender.send(saved);
        deliveryRepository.save(delivery);

        return saved;
    }
}