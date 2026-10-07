package com.challange.takehomechallange.infrastructure.adapter.in.controller;

import com.challange.takehomechallange.domain.port.in.CreateNotificationUseCase;
import com.challange.takehomechallange.domain.port.in.DeleteNotificationUseCase;
import com.challange.takehomechallange.domain.port.in.ListNotificationsUseCase;
import com.challange.takehomechallange.domain.port.in.UpdateNotificationUseCase;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.NotificationRequest;
import com.challange.takehomechallange.infrastructure.adapter.in.controller.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones")
@SecurityRequirement(name = "bearerAuth") // pide el token en Swagger (boton Authorize)
public class NotificationController {

    private final CreateNotificationUseCase createNotification;
    private final UpdateNotificationUseCase updateNotification;
    private final DeleteNotificationUseCase deleteNotification;
    private final ListNotificationsUseCase listNotifications;

    public NotificationController(CreateNotificationUseCase createNotification,
                                  UpdateNotificationUseCase updateNotification,
                                  DeleteNotificationUseCase deleteNotification,
                                  ListNotificationsUseCase listNotifications) {
        this.createNotification = createNotification;
        this.updateNotification = updateNotification;
        this.deleteNotification = deleteNotification;
        this.listNotifications = listNotifications;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una notificacion (se envia por el canal indicado)")
    public NotificationResponse create(@AuthenticationPrincipal UUID userId,
                                       @Valid @RequestBody NotificationRequest request) {
        var created = createNotification.create(new CreateNotificationUseCase.Command(
                userId, request.title(), request.content(), request.channel(), request.recipient()));
        return NotificationResponse.from(created);
    }

    @GetMapping
    @Operation(summary = "Listar mis notificaciones")
    public List<NotificationResponse> list(@AuthenticationPrincipal UUID userId) {
        return listNotifications.listByUser(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar una notificacion propia")
    public NotificationResponse update(@AuthenticationPrincipal UUID userId,
                                       @PathVariable UUID id,
                                       @Valid @RequestBody NotificationRequest request) {
        var updated = updateNotification.update(new UpdateNotificationUseCase.Command(
                id, userId, request.title(), request.content(), request.channel(), request.recipient()));
        return NotificationResponse.from(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una notificacion propia")
    public void delete(@AuthenticationPrincipal UUID userId, @PathVariable UUID id) {
        deleteNotification.delete(id, userId);
    }
}