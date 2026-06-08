package com.litethinking.backend.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear o actualizar una {@code Empresa}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * validados desde el adaptador de entrada (controlador REST) hacia el caso de uso.
 * Las anotaciones de validación de Bean Validation se aplican sobre los componentes
 * del record.</p>
 *
 * <p><strong>Validaciones aplicadas:</strong></p>
 * <ul>
 *   <li>{@code nit}: obligatorio, formato colombiano {@code [6-10 dígitos]-[dígito verificación]}.</li>
 *   <li>{@code nombre}: obligatorio, máximo 200 caracteres.</li>
 *   <li>{@code direccion}: obligatoria, máximo 300 caracteres.</li>
 *   <li>{@code telefono}: opcional, máximo 30 caracteres.</li>
 * </ul>
 */
public record EmpresaRequest(

        @NotBlank(message = "El NIT es obligatorio")
        @Pattern(
                regexp = "^[0-9]{6,10}-[0-9]$",
                message = "Formato de NIT inválido. Ejemplo: 900123456-1"
        )
        String nit,

        @NotBlank(message = "El nombre de la empresa es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
        String direccion,

        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String telefono

) {}
