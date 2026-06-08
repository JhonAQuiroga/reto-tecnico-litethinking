package com.litethinking.backend.domain.exception;

public class UsuarioNotFoundException extends DomainException {

    public UsuarioNotFoundException(String email) {
        super("No se encontró ningún usuario con email: " + email);
    }
}
