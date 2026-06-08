package com.litethinking.backend.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para solicitar el envío del reporte PDF de inventario por correo electrónico.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 utilizado en el caso de uso
 * {@link com.litethinking.backend.application.port.in.inventario.GestionarInventarioUseCase#generarYEnviarPdfPorCorreo}
 * y en {@link com.litethinking.backend.application.port.in.inventario.GenerarPdfInventarioUseCase#generarYEnviarPorCorreo}.</p>
 *
 * <p><strong>Validaciones aplicadas:</strong></p>
 * <ul>
 *   <li>{@code destinatario}: obligatorio, debe ser un email válido según RFC 5321.</li>
 *   <li>{@code asunto}: obligatorio, máximo 150 caracteres.</li>
 *   <li>{@code mensajeAdicional}: opcional, máximo 500 caracteres.</li>
 *   <li>{@code empresaNit}: obligatorio — filtra el inventario por empresa.
 *       Si se desea exportar el inventario de todas las empresas, se debe invocar
 *       el endpoint con un NIT específico.</li>
 * </ul>
 */
public record InventarioPdfEmailRequest(

        @NotBlank(message = "El correo destinatario es obligatorio")
        @Email(message = "El formato del email es inválido")
        String destinatario,

        @NotBlank(message = "El asunto del correo es obligatorio")
        @Size(max = 150, message = "El asunto no puede superar los 150 caracteres")
        String asunto,

        @Size(max = 500, message = "El mensaje adicional no puede superar los 500 caracteres")
        String mensajeAdicional,

        @NotBlank(message = "El NIT de la empresa es obligatorio para filtrar el inventario")
        String empresaNit

) {}
