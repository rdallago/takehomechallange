package com.challange.takehomechallange.application.service;

import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.port.in.ListNotificationsUseCase;
import com.challange.takehomechallange.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListNotificationsService implements ListNotificationsUseCase {

    private final NotificationRepositoryPort notificationRepository;

    public ListNotificationsService(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> listByUser(UUID userId) {
        return notificationRepository.findAllByUserId(userId);
    }
}