package com.litethinking.backend.domain.exception;

public class EmpresaNotFoundException extends DomainException {

    public EmpresaNotFoundException(String nit) {
        super("No se encontró ninguna empresa con NIT: " + nit);
    }
}
