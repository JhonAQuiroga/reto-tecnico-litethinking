package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.exception.DomainException;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Inventario.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>El {@code Inventario} representa el stock disponible de un {@link Producto}
 * dentro de una {@link Empresa} específica. Es el punto central de las reglas
 * de negocio relacionadas con la disponibilidad de mercancía.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El NIT de empresa y el código de producto son obligatorios.</li>
 *   <li>La cantidad nunca puede ser negativa.</li>
 *   <li>No se puede reducir el stock por debajo de cero
 *       (se lanza {@link StockInsuficienteException} si se intenta).</li>
 * </ul>
 */
public final class Inventario {

    private final Long          id;
    private final String        empresaNit;
    private final String        productoCodigo;
    /** Cantidad disponible en stock. Siempre >= 0. */
    private int                 cantidad;
    private final ZonedDateTime creadoEn;
    private ZonedDateTime       actualizadoEn;

    /** Relaciones resueltas: se populan al construir proyecciones enriquecidas. */
    private final Empresa   empresa;
    private final Producto  producto;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Inventario}.
     *
     * @param id              identificador de base de datos ({@code null} si aún no persistido).
     * @param empresaNit      NIT de la empresa dueña del stock; nunca {@code null} ni en blanco.
     * @param productoCodigo  código del producto en stock; nunca {@code null} ni en blanco.
     * @param cantidad        cantidad inicial disponible; nunca negativa.
     * @param creadoEn        timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn   timestamp de última actualización; puede ser {@code null}.
     * @param empresa         referencia resuelta a la Empresa (puede ser {@code null}
     *                        en contextos donde solo se necesitan los IDs).
     * @param producto        referencia resuelta al Producto (puede ser {@code null}
     *                        en contextos donde solo se necesitan los IDs).
     * @throws NullPointerException     si {@code empresaNit} o {@code productoCodigo} son {@code null}.
     * @throws IllegalArgumentException si {@code cantidad} es negativa.
     */
    public Inventario(Long id,
                      String empresaNit,
                      String productoCodigo,
                      int cantidad,
                      ZonedDateTime creadoEn,
                      ZonedDateTime actualizadoEn,
                      Empresa empresa,
                      Producto producto) {

        Objects.requireNonNull(empresaNit,     "El NIT de empresa no puede ser nulo.");
        Objects.requireNonNull(productoCodigo, "El código de producto no puede ser nulo.");

        if (empresaNit.isBlank()) {
            throw new IllegalArgumentException("El NIT de empresa no puede estar en blanco.");
        }
        if (productoCodigo.isBlank()) {
            throw new IllegalArgumentException("El código de producto no puede estar en blanco.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de inventario no puede ser negativa. Recibida: " + cantidad + "."
            );
        }

        this.id             = id;
        this.empresaNit     = empresaNit;
        this.productoCodigo = productoCodigo;
        this.cantidad       = cantidad;
        this.creadoEn       = creadoEn;
        this.actualizadoEn  = actualizadoEn;
        this.empresa        = empresa;
        this.producto       = producto;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getEmpresaNit() {
        return empresaNit;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public ZonedDateTime getCreadoEn() {
        return creadoEn;
    }

    public ZonedDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Producto getProducto() {
        return producto;
    }

    // -------------------------------------------------------------------------
    // Métodos de negocio (lógica de stock — núcleo del dominio)
    // -------------------------------------------------------------------------

    /**
     * Verifica si hay suficiente stock para satisfacer una cantidad solicitada.
     *
     * @param cantidadSolicitada cantidad requerida; debe ser positiva.
     * @return {@code true} si {@code cantidad >= cantidadSolicitada}.
     * @throws IllegalArgumentException si {@code cantidadSolicitada} es menor o igual a cero.
     */
    public boolean hayDisponibilidad(int cantidadSolicitada) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad solicitada debe ser mayor a cero. Recibida: " + cantidadSolicitada + "."
            );
        }
        return this.cantidad >= cantidadSolicitada;
    }

    /**
     * Reduce el stock disponible en la cantidad indicada.
     *
     * <p>Se invoca al confirmar una orden de compra. Si el stock resultante
     * sería negativo, se lanza {@link StockInsuficienteException} en lugar de
     * permitir un estado inválido.</p>
     *
     * @param cantidadAReducir cantidad a descontar del stock; debe ser positiva.
     * @param ahora            timestamp del momento de la operación para auditoría.
     * @throws IllegalArgumentException   si {@code cantidadAReducir} es menor o igual a cero.
     * @throws StockInsuficienteException si el stock disponible es insuficiente.
     */
    public void reducirStock(int cantidadAReducir, ZonedDateTime ahora) {
        if (cantidadAReducir <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a reducir debe ser mayor a cero. Recibida: " + cantidadAReducir + "."
            );
        }
        if (!hayDisponibilidad(cantidadAReducir)) {
            throw new StockInsuficienteException(productoCodigo, this.cantidad, cantidadAReducir);
        }
        this.cantidad      -= cantidadAReducir;
        this.actualizadoEn  = ahora;
    }

    /**
     * Agrega unidades al stock disponible (reposición de inventario).
     *
     * @param cantidadAAgregar cantidad a sumar al stock; debe ser positiva.
     * @param ahora            timestamp del momento de la operación para auditoría.
     * @throws IllegalArgumentException si {@code cantidadAAgregar} es menor o igual a cero.
     */
    public void agregarStock(int cantidadAAgregar, ZonedDateTime ahora) {
        if (cantidadAAgregar <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a agregar debe ser mayor a cero. Recibida: " + cantidadAAgregar + "."
            );
        }
        this.cantidad      += cantidadAAgregar;
        this.actualizadoEn  = ahora;
    }

    /**
     * Establece el stock en una cantidad absoluta.
     *
     * @param nuevoStock cantidad absoluta de stock; no puede ser negativa.
     * @param ahora      timestamp del momento de la operación para auditoría.
     * @throws IllegalArgumentException si {@code nuevoStock} es negativo.
     */
    public void establecerStock(int nuevoStock, ZonedDateTime ahora) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                    "El nuevo stock no puede ser negativo. Recibido: " + nuevoStock + "."
            );
        }
        this.cantidad      = nuevoStock;
        this.actualizadoEn  = ahora;
    }

    /**
     * Indica si el producto está agotado (stock igual a cero).
     *
     * @return {@code true} si no hay unidades disponibles.
     */
    public boolean estaAgotado() {
        return this.cantidad == 0;
    }

    // -------------------------------------------------------------------------
    // Excepción de dominio anidada (cohesión: pertenece al contexto de Inventario)
    // -------------------------------------------------------------------------

    /**
     * Excepción de dominio lanzada cuando se intenta reducir el stock
     * por encima de la cantidad disponible.
     */
    public static final class StockInsuficienteException extends DomainException {

        private final String productoCodigo;
        private final int    stockActual;
        private final int    cantidadSolicitada;

        public StockInsuficienteException(String productoCodigo, int stockActual, int cantidadSolicitada) {
            super(String.format(
                    "Stock insuficiente para el producto '%s'. " +
                    "Disponible: %d unidades. Solicitado: %d unidades.",
                    productoCodigo, stockActual, cantidadSolicitada
            ));
            this.productoCodigo     = productoCodigo;
            this.cantidadSolicitada = cantidadSolicitada;
            this.stockActual        = stockActual;
        }

        public String getProductoCodigo() {
            return productoCodigo;
        }

        public int getStockActual() {
            return stockActual;
        }

        public int getCantidadSolicitada() {
            return cantidadSolicitada;
        }
    }

    // -------------------------------------------------------------------------
    // Igualdad por empresa + producto (identidad de negocio)
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventario other)) return false;
        return empresaNit.equals(other.empresaNit) &&
               productoCodigo.equals(other.productoCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empresaNit, productoCodigo);
    }

    @Override
    public String toString() {
        return "Inventario{empresa=" + empresaNit + ", producto=" + productoCodigo +
               ", cantidad=" + cantidad + "}";
    }
}
