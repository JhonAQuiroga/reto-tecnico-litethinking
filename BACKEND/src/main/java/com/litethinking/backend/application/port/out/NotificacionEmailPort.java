package com.litethinking.backend.application.port.out;

/**
 * Puerto de salida: contrato para el envío de notificaciones por correo electrónico.
 *
 * <p>Clean Architecture — Application Layer: este puerto desacopla completamente la
 * lógica de negocio (casos de uso) del proveedor de email concreto. La implementación
 * puede ser SMTP via JavaMailSender, SendGrid, Amazon SES o cualquier otro proveedor,
 * sin que los casos de uso necesiten cambiar.</p>
 *
 * <p>El uso principal de este puerto es el envío del reporte PDF de inventario
 * por correo electrónico, requerimiento no funcional del reto técnico.</p>
 */
public interface NotificacionEmailPort {

    /**
     * Envía un correo electrónico con un archivo PDF adjunto.
     *
     * <p>Este método es <strong>síncrono</strong>: bloquea hasta que el correo haya
     * sido aceptado por el servidor SMTP (o equivalente del proveedor). Si el envío
     * falla, se lanza una excepción de runtime que el caso de uso puede manejar.</p>
     *
     * @param destinatario  dirección de correo del receptor (ej.: "gerente@empresa.com");
     *                      nunca {@code null} ni en blanco.
     * @param asunto        línea de asunto del mensaje; nunca {@code null} ni en blanco.
     * @param cuerpo        cuerpo del mensaje en texto plano o HTML; puede ser {@code null},
     *                      en cuyo caso se envía sin cuerpo de texto.
     * @param pdfBytes      contenido binario del archivo PDF adjunto; nunca {@code null}.
     * @param nombreArchivo nombre del archivo adjunto tal como lo verá el destinatario
     *                      (ej.: "inventario-empresa-900123456-1.pdf"); nunca {@code null}.
     * @throws IllegalArgumentException si {@code destinatario} o {@code asunto} están en blanco.
     * @throws RuntimeException         si ocurre un error al comunicarse con el proveedor de email.
     */
    void enviarConPdfAdjunto(
            String destinatario,
            String asunto,
            String cuerpo,
            byte[] pdfBytes,
            String nombreArchivo
    );

    /**
     * Envía un correo electrónico simple sin adjuntos.
     *
     * <p>Útil para notificaciones de confirmación, alertas de stock bajo, etc.</p>
     *
     * @param destinatario dirección de correo del receptor; nunca {@code null} ni en blanco.
     * @param asunto       línea de asunto del mensaje; nunca {@code null} ni en blanco.
     * @param cuerpo       cuerpo del mensaje en texto plano o HTML; puede ser {@code null}.
     */
    void enviarNotificacion(
            String destinatario,
            String asunto,
            String cuerpo
    );
}
