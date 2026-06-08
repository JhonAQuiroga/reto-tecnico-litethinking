package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.valueobject.Dinero;

import java.util.Objects;

/**
 * Objeto de valor embebido: línea de producto dentro de una {@link Orden}.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>{@code OrdenProducto} representa una <em>línea de detalle</em> de la orden.
 * Captura el <strong>snapshot del precio unitario en COP al momento de la compra</strong>,
 * lo que garantiza que el histórico de órdenes no se vea afectado por cambios
 * futuros en el precio del producto.</p>
 *
 * <p>Su diseño como Value Object implica que no tiene identidad propia; existe
 * únicamente en el contexto del agregado {@link Orden}.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El código de producto y el precio unitario son obligatorios.</li>
 *   <li>La cantidad debe ser al menos 1 unidad.</li>
 *   <li>El precio unitario es un snapshot inmutable en la moneda original de la transacción.</li>
 * </ul>
 */
public final class OrdenProducto {

    private final String   productoCodigo;
    private final int      cantidad;
    /**
     * Precio unitario al momento de la orden (snapshot inmutable).
     * Tipicamente en COP, pero se preserva la moneda dentro del VO Dinero
     * para mantener trazabilidad.
     */
    private final Dinero   precioUnitario;

    /** Relación resuelta: se popula en proyecciones enriquecidas. Puede ser null. */
    private final Producto producto;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una línea de producto para una orden de compra.
     *
     * @param productoCodigo código del producto incluido en la orden;
     *                       nunca {@code null} ni en blanco.
     * @param cantidad       cantidad de unidades solicitadas; mínimo 1.
     * @param precioUnitario precio unitario snapshot al momento de la orden;
     *                       nunca {@code null}.
     * @param producto       referencia resuelta al Producto (puede ser {@code null}
     *                       si solo se requieren los IDs de clave foránea).
     * @throws NullPointerException     si {@code productoCodigo} o {@code precioUnitario}
     *                                  son {@code null}.
     * @throws IllegalArgumentException si {@code productoCodigo} está en blanco
     *                                  o si {@code cantidad} es menor a 1.
     */
    public OrdenProducto(String productoCodigo,
                         int cantidad,
                         Dinero precioUnitario,
                         Producto producto) {

        Objects.requireNonNull(productoCodigo, "El código de producto no puede ser nulo.");
        Objects.requireNonNull(precioUnitario, "El precio unitario no puede ser nulo.");

        if (productoCodigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de producto de la línea de orden no puede estar en blanco."
            );
        }
        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad de una línea de orden debe ser al menos 1. Recibida: " + cantidad + "."
            );
        }

        this.productoCodigo = productoCodigo;
        this.cantidad       = cantidad;
        this.precioUnitario = precioUnitario;
        this.producto       = producto;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    /**
     * @return precio unitario snapshot en el momento de la orden como {@link Dinero};
     *         nunca {@code null}.
     */
    public Dinero getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * @return referencia resuelta al Producto; puede ser {@code null}
     *         en contextos donde solo se necesitan los IDs.
     */
    public Producto getProducto() {
        return producto;
    }

    // -------------------------------------------------------------------------
    // Métodos de negocio
    // -------------------------------------------------------------------------

    /**
     * Calcula el subtotal de esta línea: precio unitario × cantidad.
     *
     * <p>El resultado está expresado en la misma moneda que el precio unitario
     * (heredada del VO {@link Dinero}).</p>
     *
     * @return instancia de {@link Dinero} con el subtotal calculado.
     */
    public Dinero calcularSubtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    // -------------------------------------------------------------------------
    // Igualdad por productoCodigo (en contexto de la misma orden)
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdenProducto other)) return false;
        return productoCodigo.equals(other.productoCodigo) &&
               cantidad == other.cantidad &&
               precioUnitario.equals(other.precioUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoCodigo, cantidad, precioUnitario);
    }

    @Override
    public String toString() {
        return "OrdenProducto{producto='" + productoCodigo + "', cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario +
               ", subtotal=" + calcularSubtotal() + "}";
    }
}
