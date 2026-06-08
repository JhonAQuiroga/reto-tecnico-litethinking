package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.ProductoRequest;
import com.litethinking.backend.application.dto.response.ProductoResponse;
import com.litethinking.backend.application.port.in.producto.ActualizarProductoUseCase;
import com.litethinking.backend.application.port.in.producto.ConsultarProductoUseCase;
import com.litethinking.backend.application.port.in.producto.CrearProductoUseCase;
import com.litethinking.backend.application.port.in.producto.EliminarProductoUseCase;
import com.litethinking.backend.application.port.out.CategoriaRepositoryPort;
import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.application.port.out.InventarioRepositoryPort;
import com.litethinking.backend.application.port.out.ProductoRepositoryPort;
import com.litethinking.backend.domain.enums.Moneda;
import com.litethinking.backend.domain.exception.EmpresaNotFoundException;
import com.litethinking.backend.domain.exception.ProductoNotFoundException;
import com.litethinking.backend.domain.exception.RecursoYaExisteException;
import com.litethinking.backend.domain.model.Categoria;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.model.Inventario;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Servicio de aplicación para el CRUD de Productos.
 *
 * <p>Clean Architecture — Application Layer: orquesta los casos de uso relacionados
 * con la entidad {@link Producto}. Gestiona también la relación Many-to-Many con
 * {@link Categoria} y el registro automático en inventario (con cantidad inicial 0)
 * al crear un producto.</p>
 *
 * <p>Usa exclusivamente el constructor explícito de {@link Producto} (dominio puro,
 * sin Lombok), respetando las invariantes del dominio: VOs {@link CodigoProducto},
 * {@link Dinero} y {@link NitEmpresa}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService implements
        CrearProductoUseCase,
        ActualizarProductoUseCase,
        EliminarProductoUseCase,
        ConsultarProductoUseCase {

    private final ProductoRepositoryPort   productoRepositoryPort;
    private final EmpresaRepositoryPort    empresaRepositoryPort;
    private final CategoriaRepositoryPort  categoriaRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    // -------------------------------------------------------------------------
    // ConsultarProductoUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepositoryPort.listarTodos().stream()
                .map(p -> ProductoResponse.desde(p))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorEmpresa(String empresaNit) {
        return productoRepositoryPort.listarPorEmpresa(empresaNit).stream()
                .map(p -> ProductoResponse.desde(p))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorCodigo(String codigo) {
        Producto producto = productoRepositoryPort.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ProductoNotFoundException(codigo));
        return ProductoResponse.desde(producto);
    }

    // -------------------------------------------------------------------------
    // CrearProductoUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ProductoResponse ejecutar(ProductoRequest request) {
        if (productoRepositoryPort.existePorCodigo(request.codigo())) {
            throw new RecursoYaExisteException("Producto", request.codigo());
        }

        // Verificar que la empresa existe antes de crear el producto
        Empresa empresa = empresaRepositoryPort.buscarPorNit(request.empresaNit())
                .orElseThrow(() -> new EmpresaNotFoundException(request.empresaNit()));

        List<Categoria> categorias = resolverCategorias(request.categoriaIds());

        // Construcción con constructor explícito del dominio puro
        Producto producto = new Producto(
                new CodigoProducto(request.codigo()),
                request.nombre(),
                request.caracteristicas(),
                new Dinero(request.precioCop(), Moneda.COP),
                new Dinero(request.precioUsd(), Moneda.USD),
                new Dinero(request.precioEur(), Moneda.EUR),
                new NitEmpresa(request.empresaNit()),
                true,       // activo por defecto
                categorias,
                null,       // creadoEn — asignado por la BD/auditoría JPA
                null        // actualizadoEn — asignado por la BD/auditoría JPA
        );

        Producto guardado = productoRepositoryPort.guardar(producto);

        // Registrar en inventario con la cantidad inicial
        int stock = (request.stockInicial() != null && request.stockInicial() >= 0) ? request.stockInicial() : 0;
        Inventario inventarioInicial = new Inventario(
                null,                       // id — aún no persistido
                request.empresaNit(),
                guardado.getCodigoValor(),
                stock,                      // cantidad inicial asignada
                null,                       // creadoEn — asignado por la BD
                null,                       // actualizadoEn — asignado por la BD
                empresa,
                guardado
        );
        inventarioRepositoryPort.guardar(inventarioInicial);

        log.info("Producto '{}' creado y registrado en inventario para empresa '{}'",
                guardado.getCodigoValor(), guardado.getEmpresaNitValor());
        return ProductoResponse.desde(guardado, empresa.getNombre());
    }

    // -------------------------------------------------------------------------
    // ActualizarProductoUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ProductoResponse ejecutar(String codigo, ProductoRequest request) {
        Producto existente = productoRepositoryPort.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ProductoNotFoundException(codigo));

        List<Categoria> categorias = resolverCategorias(request.categoriaIds());

        // Re-construir con datos actualizados manteniendo el código original e invariantes
        Producto actualizado = new Producto(
                existente.getCodigo(),              // el código es inmutable
                request.nombre(),
                request.caracteristicas(),
                new Dinero(request.precioCop(), Moneda.COP),
                new Dinero(request.precioUsd(), Moneda.USD),
                new Dinero(request.precioEur(), Moneda.EUR),
                existente.getEmpresaNit(),          // la empresa propietaria no cambia
                existente.isActivo(),               // el estado activo se preserva
                categorias,
                existente.getCreadoEn(),            // preservar timestamp original de creación
                null                                // actualizadoEn — BD lo asigna
        );

        Producto guardado = productoRepositoryPort.guardar(actualizado);
        log.info("Producto '{}' actualizado.", guardado.getCodigoValor());
        return ProductoResponse.desde(guardado);
    }

    // -------------------------------------------------------------------------
    // EliminarProductoUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void ejecutar(String codigo) {
        if (!productoRepositoryPort.existePorCodigo(codigo)) {
            throw new ProductoNotFoundException(codigo);
        }
        
        // Se debe eliminar primero los registros de inventario que referencian este producto
        // para no violar la llave foránea 'fk_inv_producto'.
        inventarioRepositoryPort.eliminarPorProductoCodigo(codigo);
        
        productoRepositoryPort.eliminar(codigo);
        log.info("Producto '{}' eliminado.", codigo);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Resuelve la lista de objetos {@link Categoria} a partir de sus IDs.
     * Devuelve una lista vacía si {@code ids} es {@code null} o está vacía.
     */
    private List<Categoria> resolverCategorias(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return categoriaRepositoryPort.buscarPorIds(ids);
    }
}
