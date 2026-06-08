package com.litethinking.backend.domain.valueobject;

import com.litethinking.backend.domain.enums.Moneda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object que encapsula un monto monetario junto con su moneda.
 *
 * <p><strong>Reglas de dominio encapsuladas:</strong></p>
 * <ul>
 *   <li>El monto nunca puede ser {@code null} ni negativo.</li>
 *   <li>La moneda nunca puede ser {@code null}.</li>
 *   <li>Todos los montos se almacenan con escala de 2 decimales y redondeo {@code HALF_UP}.</li>
 *   <li>Las operaciones aritméticas solo están permitidas entre instancias de la misma moneda.</li>
 * </ul>
 *
 * <p><strong>Propiedades de Value Object:</strong></p>
 * <ul>
 *   <li><em>Inmutabilidad total:</em> cada operación aritmética devuelve una nueva instancia.</li>
 *   <li><em>Igualdad por valor:</em> dos instancias son iguales si tienen el mismo monto Y la misma moneda.</li>
 *   <li><em>Auto-validación:</em> no puede existir en estado inválido; falla en construcción.</li>
 * </ul>
 *
 * <p>Este VO se usa en {@code Producto} para encapsular los precios multimoneda
 * ({@code precio_cop}, {@code precio_usd}, {@code precio_eur}) y en {@code OrdenProducto}
 * para capturar el snapshot del precio unitario al momento de la venta.</p>
 */
public final class Dinero {

    /** Escala fija de dos decimales para todos los montos monetarios. */
    private static final int ESCALA = 2;

    /** Modo de redondeo estándar financiero. */
    private static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    private final BigDecimal monto;
    private final Moneda     moneda;

    // -------------------------------------------------------------------------
    // Constructores y factories
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Dinero} con el monto y moneda indicados.
     *
     * @param monto  valor monetario; no puede ser {@code null} ni negativo.
     * @param moneda moneda en la que se expresa el monto; no puede ser {@code null}.
     * @throws NullPointerException     si {@code monto} o {@code moneda} son {@code null}.
     * @throws IllegalArgumentException si {@code monto} es negativo.
     */
    public Dinero(BigDecimal monto, Moneda moneda) {
        Objects.requireNonNull(monto,  "El monto no puede ser nulo.");
        Objects.requireNonNull(moneda, "La moneda no puede ser nula.");

        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El monto monetario no puede ser negativo. Recibido: " + monto + " " + moneda.getCodigoIso() + "."
            );
        }

        this.monto  = monto.setScale(ESCALA, REDONDEO);
        this.moneda = moneda;
    }

    /**
     * Factory method que crea un monto de {@code cero} en la moneda especificada.
     * Útil como valor neutro en acumulaciones.
     *
     * @param moneda moneda del monto cero.
     * @return instancia de {@code Dinero} con monto 0.00.
     */
    public static Dinero cero(Moneda moneda) {
        return new Dinero(BigDecimal.ZERO, moneda);
    }

    /**
     * Factory method de conveniencia para construir un monto a partir de un {@code double}.
     *
     * <p><em>Nota:</em> se recomienda preferir el constructor con {@code BigDecimal}
     * para evitar imprecisiones de punto flotante en contextos de alta precisión.</p>
     *
     * @param monto  valor como {@code double}.
     * @param moneda moneda del monto.
     * @return nueva instancia de {@code Dinero}.
     */
    public static Dinero de(double monto, Moneda moneda) {
        return new Dinero(BigDecimal.valueOf(monto), moneda);
    }

    // -------------------------------------------------------------------------
    // Operaciones aritméticas (retornan nuevas instancias — inmutabilidad)
    // -------------------------------------------------------------------------

    /**
     * Suma este monto con {@code otro}.
     *
     * @param otro dinero a sumar; debe tener la misma moneda.
     * @return nueva instancia con la suma de ambos montos.
     * @throws IllegalArgumentException si las monedas difieren.
     */
    public Dinero sumar(Dinero otro) {
        validarMismaMoneda(otro, "sumar");
        return new Dinero(this.monto.add(otro.monto), this.moneda);
    }

    /**
     * Resta {@code otro} de este monto.
     *
     * @param otro dinero a restar; debe tener la misma moneda.
     * @return nueva instancia con la diferencia de ambos montos.
     * @throws IllegalArgumentException si las monedas difieren o si el resultado es negativo.
     */
    public Dinero restar(Dinero otro) {
        validarMismaMoneda(otro, "restar");
        final BigDecimal resultado = this.monto.subtract(otro.monto);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "La resta produciría un monto negativo: " + this + " - " + otro + "."
            );
        }
        return new Dinero(resultado, this.moneda);
    }

    /**
     * Multiplica este monto por la cantidad indicada.
     *
     * @param cantidad factor entero positivo o cero.
     * @return nueva instancia con el monto multiplicado.
     * @throws IllegalArgumentException si {@code cantidad} es negativa.
     */
    public Dinero multiplicar(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad multiplicadora no puede ser negativa. Recibida: " + cantidad + "."
            );
        }
        return new Dinero(this.monto.multiply(BigDecimal.valueOf(cantidad)), this.moneda);
    }

    /**
     * Multiplica este monto por un factor decimal (ej.: 1.19 para aplicar IVA).
     *
     * @param factor factor {@code BigDecimal} positivo.
     * @return nueva instancia con el monto multiplicado y redondeado.
     * @throws NullPointerException     si {@code factor} es {@code null}.
     * @throws IllegalArgumentException si {@code factor} es negativo.
     */
    public Dinero multiplicar(BigDecimal factor) {
        Objects.requireNonNull(factor, "El factor multiplicador no puede ser nulo.");
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El factor multiplicador no puede ser negativo. Recibido: " + factor + "."
            );
        }
        return new Dinero(this.monto.multiply(factor), this.moneda);
    }

    // -------------------------------------------------------------------------
    // Comparaciones
    // -------------------------------------------------------------------------

    /**
     * Indica si este monto es mayor que {@code otro}.
     *
     * @param otro dinero a comparar; debe tener la misma moneda.
     * @return {@code true} si {@code this > otro}.
     */
    public boolean esMayorQue(Dinero otro) {
        validarMismaMoneda(otro, "comparar");
        return this.monto.compareTo(otro.monto) > 0;
    }

    /**
     * Indica si este monto es cero.
     *
     * @return {@code true} si el monto es exactamente 0.00.
     */
    public boolean esCero() {
        return this.monto.compareTo(BigDecimal.ZERO) == 0;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * @return monto con escala de 2 decimales, nunca {@code null} ni negativo.
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * @return moneda en la que se expresa el monto, nunca {@code null}.
     */
    public Moneda getMoneda() {
        return moneda;
    }

    // -------------------------------------------------------------------------
    // Validación interna
    // -------------------------------------------------------------------------

    private void validarMismaMoneda(Dinero otro, String operacion) {
        Objects.requireNonNull(otro, "El operando de la operación '" + operacion + "' no puede ser nulo.");
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException(
                    "No se pueden " + operacion + " monedas distintas: " +
                    this.moneda.getCodigoIso() + " vs " + otro.moneda.getCodigoIso() + "."
            );
        }
    }

    // -------------------------------------------------------------------------
    // Igualdad por valor, hashCode y representación
    // -------------------------------------------------------------------------

    /**
     * Dos instancias de {@code Dinero} son iguales si tienen el mismo monto
     * (comparación numérica, ignorando escala) y la misma moneda.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dinero other)) return false;
        return monto.compareTo(other.monto) == 0 && moneda == other.moneda;
    }

    @Override
    public int hashCode() {
        return Objects.hash(monto.stripTrailingZeros(), moneda);
    }

    /**
     * Representación legible del monto monetario con símbolo de moneda.
     * Ejemplo: "$ 19999.00", "US$ 10.50", "€ 9.75".
     */
    @Override
    public String toString() {
        return moneda.formatear(monto.toPlainString());
    }
}
