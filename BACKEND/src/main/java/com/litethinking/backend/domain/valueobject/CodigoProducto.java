package com.litethinking.backend.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa el código único de identificación de un Producto.
 *
 * <p><strong>Reglas de dominio encapsuladas:</strong></p>
 * <ul>
 *   <li>El código nunca puede ser {@code null} ni estar en blanco.</li>
 *   <li>Longitud mínima: 3 caracteres. Longitud máxima: 50 caracteres.</li>
 *   <li>Solo se admiten letras, dígitos y los separadores '-' y '_'.</li>
 *   <li>Se normaliza a mayúsculas en construcción para garantizar unicidad
 *       sin distinción de caja (ej.: "prod-001" → "PROD-001").</li>
 * </ul>
 *
 * <p><strong>Propiedades de Value Object:</strong></p>
 * <ul>
 *   <li><em>Inmutabilidad:</em> todos los campos son {@code final}.</li>
 *   <li><em>Igualdad por valor:</em> dos instancias son iguales si representan el mismo código.</li>
 *   <li><em>Auto-validación:</em> no puede existir en estado inválido; falla en construcción.</li>
 * </ul>
 *
 * <p>El código se corresponde con la PK natural de la tabla
 * {@code litethinking.productos} (columna {@code codigo}).</p>
 */
public final class CodigoProducto {

    /** Expresión regular que valida el formato del código de producto. */
    private static final String PATRON_CODIGO = "^[A-Za-z0-9\\-_]{3,50}$";

    private static final int LONGITUD_MINIMA = 3;
    private static final int LONGITUD_MAXIMA = 50;

    private final String valor;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia válida de {@code CodigoProducto}.
     *
     * <p>El valor recibido es normalizado a mayúsculas para garantizar
     * consistencia en comparaciones y almacenamiento.</p>
     *
     * @param valor cadena que representa el código de producto (ej.: "PROD-001", "LAP_15X").
     * @throws NullPointerException     si {@code valor} es {@code null}.
     * @throws IllegalArgumentException si el código no cumple las reglas de formato.
     */
    public CodigoProducto(String valor) {
        Objects.requireNonNull(valor, "El código de producto no puede ser nulo.");

        final String normalizado = valor.trim().toUpperCase();

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de producto no puede estar en blanco."
            );
        }
        if (normalizado.length() < LONGITUD_MINIMA || normalizado.length() > LONGITUD_MAXIMA) {
            throw new IllegalArgumentException(
                    "El código de producto debe tener entre " + LONGITUD_MINIMA +
                    " y " + LONGITUD_MAXIMA + " caracteres. " +
                    "Recibido: '" + valor + "' (" + normalizado.length() + " caracteres)."
            );
        }
        if (!normalizado.matches(PATRON_CODIGO)) {
            throw new IllegalArgumentException(
                    "El código de producto solo puede contener letras, dígitos, " +
                    "guiones (-) y guiones bajos (_). Recibido: '" + valor + "'."
            );
        }

        this.valor = normalizado;
    }

    // -------------------------------------------------------------------------
    // Factory method de conveniencia
    // -------------------------------------------------------------------------

    /**
     * Método de fábrica estático para una construcción más expresiva.
     *
     * <pre>{@code
     *   CodigoProducto codigo = CodigoProducto.de("PROD-001");
     * }</pre>
     *
     * @param valor cadena que representa el código de producto.
     * @return nueva instancia válida de {@code CodigoProducto}.
     */
    public static CodigoProducto de(String valor) {
        return new CodigoProducto(valor);
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * Devuelve el código normalizado a mayúsculas (ej.: "PROD-001").
     *
     * @return valor del código de producto, nunca {@code null}.
     */
    public String getValor() {
        return valor;
    }

    // -------------------------------------------------------------------------
    // Igualdad por valor, hashCode y representación
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodigoProducto other)) return false;
        return valor.equals(other.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    /**
     * Devuelve la representación textual del código de producto.
     * Equivalente a {@link #getValor()}.
     */
    @Override
    public String toString() {
        return valor;
    }
}
