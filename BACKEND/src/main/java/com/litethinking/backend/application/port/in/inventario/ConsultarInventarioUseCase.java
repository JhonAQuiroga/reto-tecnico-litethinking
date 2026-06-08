package com.litethinking.backend.application.port.in.inventario;

import com.litethinking.backend.application.dto.response.InventarioResponse;

import java.util.List;

/**
 * Puerto de entrada: casos de uso para <strong>consultar el inventario</strong>.
 *
 * <p>Clean Architecture — Application Layer: agrupa las operaciones de lectura
 * del inventario. Se ejecutan en transacciones de solo lectura.</p>
 *
 * <p>Para las operaciones de mutación de stock y generación de PDF, ver
 * {@link GestionarInventarioUseCase}.</p>
 */
public interface ConsultarInventarioUseCase {

    /**
     * Recupera todos los registros de inventario del sistema.
     *
     * @return lista de {@link InventarioResponse}; nunca {@code null}, puede estar vacía.
     */
    List<InventarioResponse> listarTodos();

    /**
     * Recupera todos los registros de inventario de una empresa específica.
     *
     * @param empresaNit NIT de la empresa (ej.: "900123456-1"); nunca {@code null}.
     * @return lista de {@link InventarioResponse} de la empresa; nunca {@code null}.
     */
    List<InventarioResponse> listarPorEmpresa(String empresaNit);
}
