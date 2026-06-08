package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.InventarioRepositoryPort;
import com.litethinking.backend.domain.enums.Moneda;
import com.litethinking.backend.domain.model.Categoria;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.model.Inventario;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.EmpresaEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.InventarioEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.EmpresaJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.InventarioJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventarioRepositoryAdapter implements InventarioRepositoryPort {

    private final InventarioJpaRepository inventarioJpaRepository;
    private final EmpresaJpaRepository empresaJpaRepository;
    private final ProductoJpaRepository productoJpaRepository;

    @Override
    public Inventario guardar(Inventario inventario) {
        EmpresaEntity empresa = empresaJpaRepository.getReferenceById(inventario.getEmpresaNit());
        ProductoEntity producto = productoJpaRepository.getReferenceById(inventario.getProductoCodigo());

        InventarioEntity entity = new InventarioEntity();
        if (inventario.getId() != null) {
            entity.setId(inventario.getId());
        }
        entity.setEmpresa(empresa);
        entity.setProducto(producto);
        entity.setCantidad(inventario.getCantidad());

        return toDomain(inventarioJpaRepository.save(entity));
    }

    @Override
    public Optional<Inventario> buscarPorEmpresaYProducto(String empresaNit, String productoCodigo) {
        return inventarioJpaRepository.findByEmpresaNitAndProductoCodigo(empresaNit, productoCodigo)
                .map(this::toDomain);
    }

    @Override
    public List<Inventario> listarTodos() {
        return inventarioJpaRepository.findAllFetched().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Inventario> listarPorEmpresa(String empresaNit) {
        return inventarioJpaRepository.findByEmpresaNitFetched(empresaNit).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        inventarioJpaRepository.deleteById(id);
    }

    @Override
    public void eliminarPorProductoCodigo(String productoCodigo) {
        inventarioJpaRepository.deleteByProductoCodigo(productoCodigo);
    }

    @Override
    public void eliminarPorEmpresaNit(String empresaNit) {
        inventarioJpaRepository.deleteByEmpresaNit(empresaNit);
    }

    private Inventario toDomain(InventarioEntity e) {
        Empresa empresa = null;
        if (e.getEmpresa() != null) {
            empresa = new Empresa(
                    new NitEmpresa(e.getEmpresa().getNit()),
                    e.getEmpresa().getNombre(),
                    e.getEmpresa().getDireccion(),
                    e.getEmpresa().getTelefono(),
                    e.getEmpresa().isActiva(),
                    e.getEmpresa().getCreatedAt(),
                    e.getEmpresa().getUpdatedAt()
            );
        }

        Producto producto = null;
        if (e.getProducto() != null) {
            List<Categoria> categorias = e.getProducto().getCategorias() != null ?
                    e.getProducto().getCategorias().stream()
                            .map(c -> new Categoria(
                                    c.getId(),
                                    c.getNombre(),
                                    c.getDescripcion(),
                                    c.getCreatedAt(),
                                    c.getUpdatedAt()
                            )).toList() : List.of();

            producto = new Producto(
                    new CodigoProducto(e.getProducto().getCodigo()),
                    e.getProducto().getNombre(),
                    e.getProducto().getCaracteristicas(),
                    new Dinero(e.getProducto().getPrecioCop(), Moneda.COP),
                    new Dinero(e.getProducto().getPrecioUsd(), Moneda.USD),
                    new Dinero(e.getProducto().getPrecioEur(), Moneda.EUR),
                    new NitEmpresa(e.getProducto().getEmpresa().getNit()),
                    e.getProducto().isActivo(),
                    categorias,
                    e.getProducto().getCreatedAt(),
                    e.getProducto().getUpdatedAt()
            );
        }

        return new Inventario(
                e.getId(),
                e.getEmpresa().getNit(),
                e.getProducto().getCodigo(),
                e.getCantidad(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                empresa,
                producto
        );
    }
}
