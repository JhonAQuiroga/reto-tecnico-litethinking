package com.litethinking.backend.domain.exception;

public class ProductoNotFoundException extends DomainException {

    public ProductoNotFoundException(String codigo) {
        super("No se encontró ningún producto con código: " + codigo);
    }
}
