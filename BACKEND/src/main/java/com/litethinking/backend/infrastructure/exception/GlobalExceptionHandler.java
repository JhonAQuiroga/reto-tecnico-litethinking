package com.litethinking.backend.infrastructure.exception;

import com.litethinking.backend.domain.exception.DomainException;
import com.litethinking.backend.domain.exception.EmpresaNotFoundException;
import com.litethinking.backend.domain.exception.ProductoNotFoundException;
import com.litethinking.backend.domain.exception.UsuarioNotFoundException;
import com.litethinking.backend.domain.exception.ClienteNotFoundException;
import com.litethinking.backend.domain.exception.OrdenNotFoundException;
import com.litethinking.backend.domain.exception.RecursoYaExisteException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones REST.
 *
 * <p>Centraliza el manejo de errores para producir respuestas consistentes
 * en toda la API, evitando la duplicación de bloques try-catch en los controladores.
 * Aplica el patrón @RestControllerAdvice de Spring MVC.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 NOT FOUND ────────────────────────────────────────────────────────

    @ExceptionHandler({
            EmpresaNotFoundException.class,
            ProductoNotFoundException.class,
            UsuarioNotFoundException.class,
            ClienteNotFoundException.class,
            OrdenNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            DomainException ex, HttpServletRequest request
    ) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    // ── 409 CONFLICT ─────────────────────────────────────────────────────────

    @ExceptionHandler(RecursoYaExisteException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            RecursoYaExisteException ex, HttpServletRequest request
    ) {
        log.warn("Conflicto de unicidad: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request));
    }

    // ── 400 BAD REQUEST — Validación de campos ────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage() : "Valor inválido",
                        (existing, replacement) -> existing
                ));

        log.warn("Error de validación: {}", errors);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los campos de la solicitud")
                .path(request.getRequestURI())
                .timestamp(ZonedDateTime.now())
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // ── 400 BAD REQUEST — Argumento ilegal de dominio ─────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request
    ) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    // ── 401 UNAUTHORIZED ─────────────────────────────────────────────────────

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            Exception ex, HttpServletRequest request
    ) {
        log.warn("Autenticación fallida: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", request));
    }

    // ── 403 FORBIDDEN ────────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request
    ) {
        log.warn("Acceso denegado a {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN,
                        "No tienes permisos para realizar esta acción", request));
    }

    // ── 500 INTERNAL SERVER ERROR ─────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request
    ) {
        log.error("Error inesperado en {}: ", request.getRequestURI(), ex);
        return ResponseEntity.internalServerError()
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error: " + ex.getMessage() + " | " + ex.getClass().getName(), request));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ApiErrorResponse buildError(HttpStatus status, String message, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(ZonedDateTime.now())
                .build();
    }
}
