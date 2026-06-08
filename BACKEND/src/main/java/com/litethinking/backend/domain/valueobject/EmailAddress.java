package com.litethinking.backend.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que encapsula y valida una dirección de correo electrónico.
 */
public final class EmailAddress {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String valor;

    public EmailAddress(String valor) {
        Objects.requireNonNull(valor, "El email no puede ser nulo");
        String normalizado = valor.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + valor);
        }
        this.valor = normalizado;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress other)) return false;
        return valor.equals(other.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
