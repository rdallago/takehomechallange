package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.exception.NotificationNotFoundException;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.in.DeleteNotificationUseCase;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteNotificationService implements DeleteNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;

    public DeleteNotificationService(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notificationRepository.delete(notification);
    }
}