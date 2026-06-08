package com.litethinking.backend.application.port.in.producto;

import com.litethinking.backend.application.dto.request.ProductoRequest;
import com.litethinking.backend.application.dto.response.ProductoResponse;

/**
 * Puerto de entrada: caso de uso para <strong>actualizar un producto existente</strong>.
 *
 * <p>Clean Architecture — Application Layer: permite modificar nombre, características,
 * precios y categorías de un producto registrado. El código del producto, como clave
 * natural de negocio, es inmutable.</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>El producto debe existir; de lo contrario se lanza una excepción.</li>
 *   <li>El código del producto no puede ser modificado.</li>
 *   <li>Los precios actualizados no pueden ser negativos.</li>
 *   <li>Las nuevas categorías deben existir en el sistema.</li>
 * </ul>
 */
public interface ActualizarProductoUseCase {

    /**
     * Ejecuta el caso de uso de actualización de producto.
     *
     * @param codigo  código del producto a actualizar (identificador de la ruta); nunca {@code null}.
     * @param request DTO con los nuevos datos a persistir; nunca {@code null}.
     * @return {@link ProductoResponse} con los datos actualizados; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.ProductoNotFoundException
     *         si no existe un producto con el código indicado.
     */
    ProductoResponse ejecutar(String codigo, ProductoRequest request);
}
