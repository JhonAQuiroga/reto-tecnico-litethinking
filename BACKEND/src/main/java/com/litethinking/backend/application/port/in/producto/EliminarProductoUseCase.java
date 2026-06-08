package com.litethinking.backend.application.port.in.producto;

/**
 * Puerto de entrada: caso de uso para <strong>eliminar un producto</strong>.
 *
 * <p>Clean Architecture — Application Layer: orquesta la eliminación de un producto
 * a partir de su código. Se eliminan también las referencias en inventario y categorías.</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>El producto debe existir; de lo contrario se lanza una excepción.</li>
 *   <li>Si el producto tiene stock disponible mayor a cero, puede requerirse confirmación
 *       (controlado por la política de infraestructura).</li>
 * </ul>
 */
public interface EliminarProductoUseCase {

    /**
     * Ejecuta el caso de uso de eliminación de producto.
     *
     * @param codigo código del producto a eliminar; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.ProductoNotFoundException
     *         si no existe un producto con el código indicado.
     */
    void ejecutar(String codigo);
}
