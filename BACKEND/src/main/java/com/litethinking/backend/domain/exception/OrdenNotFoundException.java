package com.litethinking.backend.domain.exception;

public class OrdenNotFoundException extends DomainException {

    public OrdenNotFoundException(Long id) {
        super("No se encontró ninguna orden con ID: " + id);
    }

    public OrdenNotFoundException(String numeroOrden) {
        super("No se encontró ninguna orden con número: " + numeroOrden);
    }
}
