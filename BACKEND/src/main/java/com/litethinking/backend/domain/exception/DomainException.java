package com.litethinking.backend.domain.exception;

/**
 * Excepción raíz de dominio.
 *
 * <p>Todas las excepciones de reglas de negocio deben extender esta clase.
 * Es una excepción no verificada (unchecked) para evitar contaminación
 * de las firmas de métodos en la capa de aplicación.</p>
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
