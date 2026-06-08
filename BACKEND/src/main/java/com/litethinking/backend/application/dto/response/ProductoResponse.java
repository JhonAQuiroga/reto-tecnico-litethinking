package com.litethinking.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO de respuesta para la entidad {@code Producto}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * completos de un producto desde el caso de uso hacia el adaptador de salida.
 * Incluye precios en las tres monedas soportadas (COP, USD, EUR) y las categorías
 * asociadas como lista de {@link CategoriaResponse}.</p>
 *
 * <p><strong>Campos:</strong></p>
 * <ul>
 *   <li>{@code codigo}: código único del producto (ej.: "PROD-001").</li>
 *   <li>{@code nombre}: nombre comercial del producto.</li>
 *   <li>{@code caracteristicas}: descripción técnica; puede ser {@code null}.</li>
 *   <li>{@code precioCop}: precio en pesos colombianos.</li>
 *   <li>{@code precioUsd}: precio en dólares estadounidenses.</li>
 *   <li>{@code precioEur}: precio en euros.</li>
 *   <li>{@code empresaNit}: NIT de la empresa propietaria del producto.</li>
 *   <li>{@code empresaNombre}: nombre de la empresa propietaria (denormalizado para la UI).</li>
 *   <li>{@code activo}: indica si el producto está disponible en el catálogo.</li>
 *   <li>{@code categorias}: lista de categorías asociadas; nunca {@code null}.</li>
 *   <li>{@code creadoEn}: timestamp de creación.</li>
 *   <li>{@code actualizadoEn}: timestamp de última modificación.</li>
 * </ul>
 */
public record ProductoResponse(
        String codigo,
        String nombre,
        String caracteristicas,
        BigDecimal precioCop,
        BigDecimal precioUsd,
        BigDecimal precioEur,
        String empresaNit,
        String empresaNombre,
        boolean activo,
        List<CategoriaResponse> categorias,
        ZonedDateTime creadoEn,
        ZonedDateTime actualizadoEn
) {

    /**
     * Factory method estático para construir un {@code ProductoResponse}
     * directamente desde la entidad de dominio {@link com.litethinking.backend.domain.model.Producto}.
     *
     * <p>El campo {@code empresaNombre} es opcional: pasa {@code null} si la entidad
     * de empresa no está disponible en el contexto de la consulta.</p>
     *
     * @param producto     entidad de dominio fuente; nunca {@code null}.
     * @param empresaNombre nombre de la empresa propietaria; puede ser {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     */
    public static ProductoResponse desde(
            com.litethinking.backend.domain.model.Producto producto,
            String empresaNombre) {

        List<CategoriaResponse> categoriasResp = producto.getCategorias().stream()
                .map(CategoriaResponse::desde)
                .toList();

        return new ProductoResponse(
                producto.getCodigoValor(),
                producto.getNombre(),
                producto.getCaracteristicas(),
                producto.getPrecioCop().getMonto(),
                producto.getPrecioUsd().getMonto(),
                producto.getPrecioEur().getMonto(),
                producto.getEmpresaNitValor(),
                empresaNombre,
                producto.isActivo(),
                categoriasResp,
                producto.getCreadoEn(),
                producto.getActualizadoEn()
        );
    }

    /**
     * Sobrecarga sin {@code empresaNombre} para contextos donde solo se tiene el NIT.
     *
     * @param producto entidad de dominio fuente; nunca {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     */
    public static ProductoResponse desde(com.litethinking.backend.domain.model.Producto producto) {
        return desde(producto, null);
    }
}
