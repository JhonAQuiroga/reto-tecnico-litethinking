package com.litethinking.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de respuesta para un registro de inventario.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * enriquecidos de un registro de inventario desde el caso de uso hacia el adaptador
 * de salida (controlador REST y generador de PDF).</p>
 *
 * <p>Los datos de empresa y producto están <em>denormalizados</em> (nombre, precios)
 * para evitar múltiples roundtrips en la generación del reporte de inventario.</p>
 *
 * <p><strong>Campos:</strong></p>
 * <ul>
 *   <li>{@code id}: identificador interno del registro de inventario.</li>
 *   <li>{@code empresaNit}: NIT de la empresa propietaria del stock.</li>
 *   <li>{@code empresaNombre}: nombre de la empresa (denormalizado para reportes).</li>
 *   <li>{@code productoCodigo}: código único del producto.</li>
 *   <li>{@code productoNombre}: nombre del producto (denormalizado para reportes).</li>
 *   <li>{@code productoCaracteristicas}: descripción técnica del producto.</li>
 *   <li>{@code precioCop}: precio en pesos colombianos (COP).</li>
 *   <li>{@code precioUsd}: precio en dólares estadounidenses (USD).</li>
 *   <li>{@code precioEur}: precio en euros (EUR).</li>
 *   <li>{@code cantidad}: unidades disponibles en stock (siempre &ge; 0).</li>
 *   <li>{@code agotado}: {@code true} si el stock es cero.</li>
 *   <li>{@code creadoEn}: timestamp de creación del registro de inventario.</li>
 *   <li>{@code actualizadoEn}: timestamp de última actualización del stock.</li>
 * </ul>
 */
public record InventarioResponse(
        Long id,
        String empresaNit,
        String empresaNombre,
        String productoCodigo,
        String productoNombre,
        String productoCaracteristicas,
        BigDecimal precioCop,
        BigDecimal precioUsd,
        BigDecimal precioEur,
        int cantidad,
        boolean agotado,
        ZonedDateTime creadoEn,
        ZonedDateTime actualizadoEn
) {

    /**
     * Factory method estático para construir un {@code InventarioResponse}
     * directamente desde la entidad de dominio {@link com.litethinking.backend.domain.model.Inventario}.
     *
     * <p>Requiere que la entidad de dominio tenga las referencias a {@code Empresa}
     * y {@code Producto} ya resueltas (no nulas). La resolución se realiza en el
     * adaptador de persistencia al hacer el join en la consulta JPA.</p>
     *
     * @param inventario entidad de dominio fuente con referencias resueltas; nunca {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     * @throws NullPointerException si {@code inventario.getEmpresa()} o
     *         {@code inventario.getProducto()} son {@code null}.
     */
    public static InventarioResponse desde(com.litethinking.backend.domain.model.Inventario inventario) {
        var empresa  = inventario.getEmpresa();
        var producto = inventario.getProducto();

        return new InventarioResponse(
                inventario.getId(),
                inventario.getEmpresaNit(),
                empresa  != null ? empresa.getNombre()           : null,
                inventario.getProductoCodigo(),
                producto != null ? producto.getNombre()          : null,
                producto != null ? producto.getCaracteristicas() : null,
                producto != null ? producto.getPrecioCop().getMonto() : null,
                producto != null ? producto.getPrecioUsd().getMonto() : null,
                producto != null ? producto.getPrecioEur().getMonto() : null,
                inventario.getCantidad(),
                inventario.estaAgotado(),
                inventario.getCreadoEn(),
                inventario.getActualizadoEn()
        );
    }
}
