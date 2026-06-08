package com.litethinking.backend.application.port.in.producto;

import com.litethinking.backend.application.dto.request.ProductoRequest;
import com.litethinking.backend.application.dto.response.ProductoResponse;

/**
 * Puerto de entrada: caso de uso para <strong>crear un nuevo producto</strong>.
 *
 * <p>Clean Architecture — Application Layer: contrato que expone la operación de creación
 * de producto hacia los adaptadores de entrada (controladores REST). La implementación
 * concreta es {@code ProductoService}.</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>El código del producto debe ser único en el sistema.</li>
 *   <li>La empresa referenciada por {@code empresaNit} debe existir y estar activa.</li>
 *   <li>Los tres precios (COP, USD, EUR) son obligatorios y deben ser no negativos.</li>
 *   <li>Al crear el producto, se registra automáticamente en inventario con cantidad 0.</li>
 *   <li>Las categorías referenciadas por {@code categoriaIds} deben existir.</li>
 * </ul>
 */
public interface CrearProductoUseCase {

    /**
     * Ejecuta el caso de uso de creación de producto.
     *
     * @param request DTO con los datos validados del producto a crear; nunca {@code null}.
     * @return {@link ProductoResponse} con los datos del producto recién creado,
     *         incluyendo categorías resueltas y timestamps; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.RecursoYaExisteException
     *         si ya existe un producto con el mismo código.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa referenciada no existe.
     */
    ProductoResponse ejecutar(ProductoRequest request);
}
