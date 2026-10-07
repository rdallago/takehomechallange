package com.challange.takehomechallange.infrastructure.adapter.out.sender;

import com.challange.takehomechallange.domain.exception.InvalidNotificationException;
import com.challange.takehomechallange.domain.model.Channel;
import com.challange.takehomechallange.domain.model.Notification;
import com.challange.takehomechallange.domain.model.NotificationDelivery;
import com.challange.takehomechallange.domain.port.out.NotificationSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailSender implements NotificationSenderPort {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    // Validacion simple de formato, no pretende cubrir todo el RFC
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public void validate(Notification notification) {
        String recipient = notification.getRecipient();
        if (recipient == null || !EMAIL_PATTERN.matcher(recipient).matches()) {
            throw new InvalidNotificationException("El destinatario no es un email valido: " + recipient);
        }
    }

    @Override
    public NotificationDelivery send(Notification notification) {
        // Paso 1: generar el template
        String body = buildTemplate(notification);

        // Paso 2: "enviar" (simulado, solo log)
        log.info("[EMAIL] Enviando a {} con asunto '{}'", notification.getRecipient(), notification.getTitle());

        // Paso 3: registrar el envio
        String detail = "to=" + notification.getRecipient()
                + " | subject=" + notification.getTitle()
                + " | template=" + body;
        return NotificationDelivery.sent(notification.getId(), Channel.EMAIL, detail);
    }

    private String buildTemplate(Notification n) {
        return "<html><body>"
                + "<h2>" + n.getTitle() + "</h2>"
                + "<p>" + n.getContent() + "</p>"
                + "<hr/><small>Notificacion automatica</small>"
                + "</body></html>";
    }
}