package com.litethinking.backend.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Estructura estándar de respuesta de error para todos los endpoints REST.
 * Se serializa como JSON cuando ocurre una excepción manejada.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final ZonedDateTime timestamp;

    /** Errores de validación campo a campo (solo presente en errores 400). */
    private final Map<String, String> validationErrors;
}
