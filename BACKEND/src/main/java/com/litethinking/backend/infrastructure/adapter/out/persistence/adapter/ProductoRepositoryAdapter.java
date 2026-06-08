package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.ProductoRepositoryPort;
import com.litethinking.backend.domain.enums.Moneda;
import com.litethinking.backend.domain.model.Categoria;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.CategoriaEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.EmpresaEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.CategoriaJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.EmpresaJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementación JPA del puerto {@link ProductoRepositoryPort}.
 *
 * <p>Infrastructure Layer: traduce entre la entidad de dominio {@link Producto} y la
 * entidad JPA {@link ProductoEntity}. Usa constructores explícitos del dominio puro y
 * crea los VOs {@link CodigoProducto}, {@link Dinero} y {@link NitEmpresa} correctamente.</p>
 */
@Component
@RequiredArgsConstructor
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository  productoJpaRepository;
    private final EmpresaJpaRepository   empresaJpaRepository;
    private final CategoriaJpaRepository categoriaJpaRepository;

    @Override
    public List<Producto> listarTodos() {
        return productoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Producto> listarPorEmpresa(String empresaNit) {
        return productoJpaRepository.findByEmpresaNitWithCategorias(empresaNit)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoJpaRepository.findByCodigoWithCategorias(codigo)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return productoJpaRepository.existsByCodigo(codigo);
    }

    @Override
    public Producto guardar(Producto producto) {
        // Resolver referencia de empresa (JPA lazy reference — solo necesita el ID)
        EmpresaEntity empresa = empresaJpaRepository.getReferenceById(
                producto.getEmpresaNitValor()
        );

        // Resolver referencias de categorías
        List<CategoriaEntity> cats = producto.getCategorias().stream()
                .map(c -> categoriaJpaRepository.getReferenceById(c.getId()))
                .toList();

        // Construir entidad JPA — usa setter para no depender de Lombok @Builder en sets parciales
        ProductoEntity entity = new ProductoEntity();
        entity.setCodigo(producto.getCodigoValor());
        entity.setNombre(producto.getNombre());
        entity.setCaracteristicas(producto.getCaracteristicas());
        // Extraer BigDecimal del VO Dinero para persistir
        entity.setPrecioCop(producto.getPrecioCop().getMonto());
        entity.setPrecioUsd(producto.getPrecioUsd().getMonto());
        entity.setPrecioEur(producto.getPrecioEur().getMonto());
        entity.setEmpresa(empresa);
        entity.setActivo(producto.isActivo());
        entity.setCategorias(cats);

        return toDomain(productoJpaRepository.save(entity));
    }

    @Override
    public void eliminar(String codigo) {
        productoJpaRepository.deleteById(codigo);
    }

    @Override
    public void eliminarPorEmpresa(String empresaNit) {
        productoJpaRepository.deleteByEmpresaNit(empresaNit);
    }

    // -------------------------------------------------------------------------
    // Mapeo privado
    // -------------------------------------------------------------------------

    /**
     * Convierte la entidad JPA a la entidad de dominio usando constructores explícitos.
     * Reconstruye los VOs {@link CodigoProducto}, {@link Dinero} y {@link NitEmpresa}.
     */
    private Producto toDomain(ProductoEntity e) {
        List<Categoria> categorias = e.getCategorias().stream()
                .map(c -> new Categoria(
                        c.getId(),
                        c.getNombre(),
                        c.getDescripcion(),
                        c.getCreatedAt(),
                        c.getUpdatedAt()
                ))
                .toList();

        return new Producto(
                new CodigoProducto(e.getCodigo()),
                e.getNombre(),
                e.getCaracteristicas(),
                new Dinero(e.getPrecioCop(), Moneda.COP),
                new Dinero(e.getPrecioUsd(), Moneda.USD),
                new Dinero(e.getPrecioEur(), Moneda.EUR),
                new NitEmpresa(e.getEmpresa().getNit()),
                e.isActivo(),
                categorias,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
