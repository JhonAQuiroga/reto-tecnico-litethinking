package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Inventario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato de persistencia para la entidad {@link Inventario}.
 *
 * <p>Clean Architecture — Application Layer: define las operaciones de acceso a datos
 * que los casos de uso de gestión de inventario requieren de la infraestructura.
 * La implementación concreta vive en la capa de infraestructura (adaptador JPA).</p>
 *
 * <p>El inventario modela el stock de un {@code Producto} dentro de una {@code Empresa};
 * la clave natural es la combinación {@code (empresaNit, productoCodigo)}.</p>
 */
public interface InventarioRepositoryPort {

    /**
     * Persiste un registro de inventario nuevo o actualiza uno existente.
     *
     * <p>La lógica de reducción/aumento de stock se ejecuta previamente en la entidad
     * de dominio {@link Inventario}; este método solo persiste el estado resultante.</p>
     *
     * @param inventario entidad de dominio a guardar; nunca {@code null}.
     * @return la instancia guardada con timestamps de auditoría actualizados; nunca {@code null}.
     */
    Inventario guardar(Inventario inventario);

    /**
     * Busca un registro de inventario por la combinación empresa + producto.
     *
     * @param empresaNit      valor textual del NIT de la empresa; nunca {@code null}.
     * @param productoCodigo  valor textual del código del producto; nunca {@code null}.
     * @return {@link Optional} con el inventario si existe, o vacío si no se encontró.
     */
    Optional<Inventario> buscarPorEmpresaYProducto(String empresaNit, String productoCodigo);

    /**
     * Recupera todos los registros de inventario del sistema.
     *
     * <p>Cada registro incluye las referencias resueltas a {@code Empresa} y
     * {@code Producto} para enriquecer las respuestas de la API.</p>
     *
     * @return lista de todos los registros de inventario; nunca {@code null}, puede estar vacía.
     */
    List<Inventario> listarTodos();

    /**
     * Recupera todos los registros de inventario de una empresa específica.
     *
     * <p>Incluye las referencias resueltas a {@code Producto} para facilitar
     * la generación del reporte PDF de inventario por empresa.</p>
     *
     * @param empresaNit valor textual del NIT de la empresa; nunca {@code null}.
     * @return lista de registros de inventario de la empresa; nunca {@code null}, puede estar vacía.
     */
    List<Inventario> listarPorEmpresa(String empresaNit);

    /**
     * Elimina un registro de inventario por su identificador de base de datos.
     *
     * @param id identificador numérico del registro de inventario a eliminar.
     */
    void eliminar(Long id);

    /**
     * Elimina todos los registros de inventario asociados a un producto.
     */
    void eliminarPorProductoCodigo(String productoCodigo);

    /**
     * Elimina todos los registros de inventario asociados a una empresa.
     */
    void eliminarPorEmpresaNit(String empresaNit);
}
