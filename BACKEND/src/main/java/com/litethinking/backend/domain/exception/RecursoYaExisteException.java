package com.litethinking.backend.domain.exception;

public class RecursoYaExisteException extends DomainException {

    public RecursoYaExisteException(String recurso, String identificador) {
        super("Ya existe un/a " + recurso + " con el identificador: " + identificador);
    }
}
