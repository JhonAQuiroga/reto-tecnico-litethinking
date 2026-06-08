package com.litethinking.backend.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa el Número de Identificación Tributaria (NIT) de una empresa.
 *
 * <p><strong>Reglas de dominio encapsuladas:</strong></p>
 * <ul>
 *   <li>El NIT nunca puede ser {@code null} ni estar en blanco.</li>
 *   <li>Formato válido: de 6 a 10 dígitos, guion y un dígito de verificación (ej.: 900123456-1).</li>
 *   <li>Los espacios extremos se normalizan en construcción.</li>
 * </ul>
 *
 * <p><strong>Propiedades de Value Object:</strong></p>
 * <ul>
 *   <li><em>Inmutabilidad:</em> todos los campos son {@code final}.</li>
 *   <li><em>Igualdad por valor:</em> dos instancias son iguales si representan el mismo NIT.</li>
 *   <li><em>Auto-validación:</em> no puede existir en estado inválido; falla en construcción.</li>
 * </ul>
 */
public final class NitEmpresa {

    /** Expresión regular que valida el formato XXXXXXXXX-D. */
    private static final String PATRON_NIT = "^[0-9]{6,10}-[0-9]$";

    private final String valor;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia válida de {@code NitEmpresa}.
     *
     * @param valor cadena que representa el NIT (ej.: "900123456-1").
     * @throws NullPointerException     si {@code valor} es {@code null}.
     * @throws IllegalArgumentException si el formato no cumple el patrón XXXXXXXXX-D.
     */
    public NitEmpresa(String valor) {
        Objects.requireNonNull(valor, "El NIT no puede ser nulo.");

        final String normalizado = valor.trim();

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException("El NIT no puede estar en blanco.");
        }
        if (!normalizado.matches(PATRON_NIT)) {
            throw new IllegalArgumentException(
                    "Formato de NIT inválido. Se esperaba: XXXXXXXXX-D (ej: 900123456-1). " +
                    "Recibido: '" + valor + "'."
            );
        }

        this.valor = normalizado;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * Devuelve el NIT completo con dígito de verificación (ej.: "900123456-1").
     *
     * @return representación canónica del NIT, nunca {@code null}.
     */
    public String getValor() {
        return valor;
    }

    /**
     * Devuelve únicamente la parte numérica del NIT sin el dígito de verificación.
     * Útil para integraciones con sistemas externos que reciben el número base.
     *
     * @return parte numérica (ej.: "900123456" para "900123456-1").
     */
    public String getNumeroBase() {
        return valor.substring(0, valor.indexOf('-'));
    }

    /**
     * Devuelve el dígito de verificación del NIT.
     *
     * @return carácter del dígito de verificación (ej.: '1' para "900123456-1").
     */
    public char getDigitoVerificacion() {
        return valor.charAt(valor.length() - 1);
    }

    // -------------------------------------------------------------------------
    // Igualdad por valor, hashCode y representación
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NitEmpresa other)) return false;
        return valor.equals(other.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    /**
     * Devuelve la representación textual del NIT (misma que {@link #getValor()}).
     * Útil para logs y mensajes de error.
     */
    @Override
    public String toString() {
        return valor;
    }
}
