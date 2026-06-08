package com.litethinking.backend.infrastructure.adapter.out.email;

import com.litethinking.backend.application.port.out.NotificacionEmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificacionAdapter implements NotificacionEmailPort {

    private final JavaMailSender javaMailSender;

    @Override
    public void enviarConPdfAdjunto(String destinatario, String asunto, String cuerpo, byte[] pdfBytes, String nombreArchivo) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo != null ? cuerpo : "", false);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            helper.addAttachment(nombreArchivo, resource);

            javaMailSender.send(message);
            log.info("Correo electrónico con adjunto '{}' enviado correctamente a: {}", nombreArchivo, destinatario);

        } catch (MessagingException e) {
            log.error("Error construyendo el correo electrónico para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error al construir el correo electrónico: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error enviando el correo electrónico a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error inesperado al enviar el correo: " + e.getMessage(), e);
        }
    }

    @Override
    public void enviarNotificacion(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(cuerpo != null ? cuerpo : "");

            javaMailSender.send(message);
            log.info("Notificación por correo electrónico enviada correctamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error enviando notificación por correo electrónico a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error al enviar la notificación: " + e.getMessage(), e);
        }
    }
}
