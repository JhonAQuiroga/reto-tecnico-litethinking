package com.litethinking.backend.application.port.in.producto;

import com.litethinking.backend.application.dto.response.ProductoResponse;

import java.util.List;

/**
 * Puerto de entrada: casos de uso para <strong>consultar productos</strong>.
 *
 * <p>Clean Architecture — Application Layer: agrupa todas las operaciones de lectura
 * sobre la entidad Producto. Se ejecutan en transacciones de solo lectura.</p>
 *
 * <p><strong>Operaciones disponibles:</strong></p>
 * <ul>
 *   <li>Listar todos los productos del catálogo.</li>
 *   <li>Listar productos filtrados por empresa.</li>
 *   <li>Buscar un producto específico por su código.</li>
 * </ul>
 */
public interface ConsultarProductoUseCase {

    /**
     * Recupera todos los productos registrados en el catálogo.
     *
     * <p>Cada producto incluye su lista de categorías y los datos de precios
     * en las tres monedas soportadas (COP, USD, EUR).</p>
     *
     * @return lista de {@link ProductoResponse}; nunca {@code null}, puede estar vacía.
     */
    List<ProductoResponse> listarTodos();

    /**
     * Recupera todos los productos pertenecientes a una empresa.
     *
     * @param empresaNit NIT de la empresa (ej.: "900123456-1"); nunca {@code null}.
     * @return lista de {@link ProductoResponse} de la empresa; nunca {@code null}.
     */
    List<ProductoResponse> listarPorEmpresa(String empresaNit);

    /**
     * Busca y recupera un producto por su código.
     *
     * @param codigo código del producto a buscar (ej.: "PROD-001"); nunca {@code null}.
     * @return {@link ProductoResponse} con todos los datos del producto; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.ProductoNotFoundException
     *         si no existe un producto con el código indicado.
     */
    ProductoResponse buscarPorCodigo(String codigo);
}
