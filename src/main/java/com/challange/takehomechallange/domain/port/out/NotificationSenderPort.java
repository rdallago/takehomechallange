package com.challange.takehomechallange.domain.port.out;

import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;

public interface NotificationSenderPort {

    // Canal que maneja esta estrategia (el resolver la usa para elegirla)
    Channel channel();

    // Validaciones propias del canal. Si algo esta mal, lanza InvalidNotificationException
    void validate(Notification notification);

    // Ejecuta el envio simulado y devuelve el registro con el resultado
    NotificationDelivery send(Notification notification);
}