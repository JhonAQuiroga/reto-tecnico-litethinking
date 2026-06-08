package com.litethinking.backend.domain.exception;

public class ClienteNotFoundException extends DomainException {

    public ClienteNotFoundException(Long id) {
        super("No se encontró ningún cliente con ID: " + id);
    }
}
