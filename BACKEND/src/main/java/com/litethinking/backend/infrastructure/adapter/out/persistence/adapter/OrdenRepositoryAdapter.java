package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.OrdenRepositoryPort;
import com.litethinking.backend.domain.enums.Moneda;
import com.litethinking.backend.domain.model.Cliente;
import com.litethinking.backend.domain.model.Orden;
import com.litethinking.backend.domain.model.OrdenProducto;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.OrdenProductoEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.OrdenProductoId;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.ClienteJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.OrdenJpaRepository;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdenRepositoryAdapter implements OrdenRepositoryPort {

    private final OrdenJpaRepository ordenJpaRepository;
    private final ClienteJpaRepository clienteJpaRepository;
    private final ProductoJpaRepository productoJpaRepository;

    @Override
    public List<Orden> listarTodas() {
        return ordenJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Orden> listarPorCliente(Long clienteId) {
        return ordenJpaRepository.findByClienteIdFetched(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Orden> buscarPorId(Long id) {
        return ordenJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Orden> buscarPorNumeroOrden(String numeroOrden) {
        return ordenJpaRepository.findByNumeroOrden(numeroOrden).map(this::toDomain);
    }

    @Override
    public Orden guardar(Orden orden) {
        ClienteEntity cliente = clienteJpaRepository.getReferenceById(orden.getClienteId());

        OrdenEntity entity = new OrdenEntity();
        if (orden.getId() != null) {
            entity.setId(orden.getId());
        }
        entity.setNumeroOrden(orden.getNumeroOrden());
        entity.setCliente(cliente);
        entity.setFechaOrden(orden.getFechaOrden());
        entity.setEstado(orden.getEstado());
        entity.setTotalCop(orden.getTotalCop());

        if (orden.getProductos() != null) {
            List<OrdenProductoEntity> productosEntity = orden.getProductos().stream()
                    .map(op -> {
                        ProductoEntity producto = productoJpaRepository.getReferenceById(op.getProductoCodigo());
                        OrdenProductoEntity opEntity = new OrdenProductoEntity();
                        OrdenProductoId id = new OrdenProductoId();
                        id.setProductoCodigo(op.getProductoCodigo());
                        // ordenId will be set after saving the order, but hibernate handles it if mapped correctly,
                        // actually for new orders, the id needs to be managed or mapped properly.
                        // We will set id object, but let JPA manage the keys.
                        opEntity.setId(id);
                        opEntity.setOrden(entity);
                        opEntity.setProducto(producto);
                        opEntity.setCantidad(op.getCantidad());
                        opEntity.setPrecioUnitario(op.getPrecioUnitario().getMonto());
                        return opEntity;
                    }).toList();
            entity.getProductos().addAll(productosEntity);
        }

        return toDomain(ordenJpaRepository.save(entity));
    }

    private Orden toDomain(OrdenEntity e) {
        Cliente cliente = null;
        if (e.getCliente() != null) {
            cliente = new Cliente(
                    e.getCliente().getId(),
                    e.getCliente().getNombre(),
                    e.getCliente().getEmail(),
                    e.getCliente().getTelefono(),
                    e.getCliente().getDireccion(),
                    e.getCliente().isActivo(),
                    e.getCliente().getCreatedAt(),
                    e.getCliente().getUpdatedAt()
            );
        }

        List<OrdenProducto> productos = e.getProductos().stream()
                .map(op -> {
                    Producto producto = null;
                    if (op.getProducto() != null) {
                        producto = new Producto(
                                new CodigoProducto(op.getProducto().getCodigo()),
                                op.getProducto().getNombre(),
                                op.getProducto().getCaracteristicas(),
                                new Dinero(op.getProducto().getPrecioCop(), Moneda.COP),
                                new Dinero(op.getProducto().getPrecioUsd(), Moneda.USD),
                                new Dinero(op.getProducto().getPrecioEur(), Moneda.EUR),
                                new NitEmpresa(op.getProducto().getEmpresa().getNit()),
                                op.getProducto().isActivo(),
                                List.of(), // Categories not needed here
                                op.getProducto().getCreatedAt(),
                                op.getProducto().getUpdatedAt()
                        );
                    }
                    return new OrdenProducto(
                            op.getProducto().getCodigo(),
                            op.getCantidad(),
                            new Dinero(op.getPrecioUnitario(), Moneda.COP),
                            producto
                    );
                }).toList();

        return new Orden(
                e.getId(),
                e.getNumeroOrden(),
                e.getCliente().getId(),
                cliente,
                e.getFechaOrden(),
                e.getEstado(),
                e.getTotalCop(),
                productos,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
