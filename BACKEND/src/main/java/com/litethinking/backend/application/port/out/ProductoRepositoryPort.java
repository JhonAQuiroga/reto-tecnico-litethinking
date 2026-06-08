package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato de persistencia para la entidad {@link Producto}.
 *
 * <p>Clean Architecture — Application Layer: define las operaciones de acceso a datos
 * que los casos de uso de producto requieren de la infraestructura. La implementación
 * concreta (adaptador JPA) vive en la capa de infraestructura.</p>
 */
public interface ProductoRepositoryPort {

    /**
     * Persiste un nuevo producto o actualiza uno existente.
     *
     * <p>Si el producto ya existe con el mismo código, sus datos se actualizan.
     * Si no existe, se crea un nuevo registro junto con la relación
     * producto–categoría correspondiente.</p>
     *
     * @param producto entidad de dominio a guardar; nunca {@code null}.
     * @return la instancia guardada con timestamps de auditoría poblados; nunca {@code null}.
     */
    Producto guardar(Producto producto);

    /**
     * Busca un producto por su código de producto (clave natural de negocio).
     *
     * @param codigo valor textual del código (ej.: "PROD-001"); nunca {@code null}.
     * @return {@link Optional} con el producto si existe (incluyendo sus categorías),
     *         o vacío si no se encontró.
     */
    Optional<Producto> buscarPorCodigo(String codigo);

    /**
     * Verifica si ya existe un producto con el código indicado.
     *
     * @param codigo valor textual del código a verificar; nunca {@code null}.
     * @return {@code true} si existe al menos un producto con ese código.
     */
    boolean existePorCodigo(String codigo);

    /**
     * Recupera todos los productos registrados en el sistema.
     *
     * <p>Cada producto incluye su lista de categorías resuelta.</p>
     *
     * @return lista de productos; nunca {@code null}, puede estar vacía.
     */
    List<Producto> listarTodos();

    /**
     * Recupera todos los productos pertenecientes a una empresa específica.
     *
     * @param empresaNit valor textual del NIT de la empresa propietaria; nunca {@code null}.
     * @return lista de productos de la empresa; nunca {@code null}, puede estar vacía.
     */
    List<Producto> listarPorEmpresa(String empresaNit);

    /**
     * Elimina un producto por su código.
     *
     * <p>También elimina las referencias en {@code producto_categorias}
     * y en {@code inventario} antes de ejecutar el borrado físico.</p>
     *
     * @param codigo valor textual del código del producto a eliminar; nunca {@code null}.
     */
    void eliminar(String codigo);

    /**
     * Elimina todos los productos que pertenezcan a una empresa.
     *
     * @param empresaNit valor textual del NIT de la empresa propietaria; nunca {@code null}.
     */
    void eliminarPorEmpresa(String empresaNit);
}
