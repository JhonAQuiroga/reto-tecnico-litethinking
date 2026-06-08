package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio: Orden de compra.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>Una {@code Orden} pertenece a un {@link Cliente} (Many-to-One) y contiene
 * múltiples líneas de producto a través de {@link OrdenProducto} (One-to-Many).</p>
 */
public final class Orden {

    private final Long             id;
    private final String           numeroOrden;
    private final Long             clienteId;
    private final Cliente          cliente;
    private final ZonedDateTime    fechaOrden;
    private final EstadoOrden      estado;
    private final BigDecimal       totalCop;
    private final List<OrdenProducto> productos;
    private final ZonedDateTime    creadoEn;
    private final ZonedDateTime    actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Orden(Long id,
                 String numeroOrden,
                 Long clienteId,
                 Cliente cliente,
                 ZonedDateTime fechaOrden,
                 EstadoOrden estado,
                 BigDecimal totalCop,
                 List<OrdenProducto> productos,
                 ZonedDateTime creadoEn,
                 ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(numeroOrden, "El número de orden no puede ser nulo.");
        Objects.requireNonNull(clienteId,   "El ID del cliente no puede ser nulo.");
        Objects.requireNonNull(estado,      "El estado de la orden no puede ser nulo.");

        this.id            = id;
        this.numeroOrden   = numeroOrden.trim();
        this.clienteId     = clienteId;
        this.cliente       = cliente;
        this.fechaOrden    = fechaOrden;
        this.estado        = estado;
        this.totalCop      = totalCop;
        this.productos     = (productos != null) ? List.copyOf(productos) : List.of();
        this.creadoEn      = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    public Long getId()                       { return id; }
    public String getNumeroOrden()            { return numeroOrden; }
    public Long getClienteId()                { return clienteId; }
    public Cliente getCliente()               { return cliente; }
    public ZonedDateTime getFechaOrden()      { return fechaOrden; }
    public EstadoOrden getEstado()            { return estado; }
    public BigDecimal getTotalCop()           { return totalCop; }
    public ZonedDateTime getCreadoEn()        { return creadoEn; }
    public ZonedDateTime getActualizadoEn()   { return actualizadoEn; }

    /**
     * @return vista inmutable de los productos de la orden; nunca {@code null}.
     */
    public List<OrdenProducto> getProductos() {
        return productos;
    }

    // -------------------------------------------------------------------------
    // Métodos de negocio
    // -------------------------------------------------------------------------

    /**
     * Calcula el total de la orden sumando los subtotales de cada línea de producto.
     * Se usa para validar consistencia antes de persistir.
     *
     * @return total calculado en COP.
     */
    public BigDecimal calcularTotal() {
        return getProductos().stream()
                .map(op -> op.calcularSubtotal().getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Indica si la orden está en estado pendiente (aún puede ser cancelada o modificada).
     */
    public boolean estaPendiente() {
        return EstadoOrden.PENDIENTE.equals(this.estado);
    }

    /**
     * Indica si la orden ya fue procesada (entregada/ejecutada).
     */
    public boolean estaProcesada() {
        return EstadoOrden.PROCESADA.equals(this.estado);
    }

    // -------------------------------------------------------------------------
    // Igualdad por número de orden, hashCode y representación
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Orden other)) return false;
        return numeroOrden.equals(other.numeroOrden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroOrden);
    }

    @Override
    public String toString() {
        return "Orden{id=" + id + ", numero='" + numeroOrden + "', estado=" + estado + "}";
    }
}
