package com.litethinking.backend.application.dto.response;

import com.litethinking.backend.domain.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO de respuesta para la entidad {@code Orden}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21.</p>
 */
public record OrdenResponse(
        Long id,
        String numeroOrden,
        Long clienteId,
        String clienteNombre,
        ZonedDateTime fechaOrden,
        EstadoOrden estado,
        BigDecimal totalCop,
        List<OrdenProductoResponse> productos,
        ZonedDateTime creadoEn
) {

    /**
     * DTO anidado para la línea de producto dentro de la respuesta de una orden.
     */
    public record OrdenProductoResponse(
            String productoCodigo,
            String productoNombre,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {}
}
