package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.OrdenRequest;
import com.litethinking.backend.application.dto.response.OrdenResponse;
import com.litethinking.backend.application.port.in.orden.OrdenUseCase;
import com.litethinking.backend.application.port.out.ClienteRepositoryPort;
import com.litethinking.backend.application.port.out.OrdenRepositoryPort;
import com.litethinking.backend.application.port.out.ProductoRepositoryPort;
import com.litethinking.backend.domain.enums.EstadoOrden;
import com.litethinking.backend.domain.exception.ClienteNotFoundException;
import com.litethinking.backend.domain.exception.OrdenNotFoundException;
import com.litethinking.backend.domain.exception.ProductoNotFoundException;
import com.litethinking.backend.domain.model.Orden;
import com.litethinking.backend.domain.model.OrdenProducto;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.enums.Moneda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación para el ciclo de vida de Órdenes.
 *
 * <p>Clean Architecture — Application Layer: orquesta la creación y consulta de órdenes.
 * Usa constructores explícitos de las entidades de dominio puro.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenService implements OrdenUseCase {

    private final OrdenRepositoryPort    ordenRepositoryPort;
    private final ClienteRepositoryPort  clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarTodas() {
        return ordenRepositoryPort.listarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarPorCliente(Long clienteId) {
        return ordenRepositoryPort.listarPorCliente(clienteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponse buscarPorId(Long id) {
        return toResponse(ordenRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new OrdenNotFoundException(id)));
    }

    @Override
    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        clienteRepositoryPort.buscarPorId(request.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException(request.getClienteId()));

        // Construir líneas de producto con el constructor explícito del dominio
        List<OrdenProducto> lineas = request.getProductos().stream()
                .map(item -> {
                    Producto producto = productoRepositoryPort.buscarPorCodigo(item.getProductoCodigo())
                            .orElseThrow(() -> new ProductoNotFoundException(item.getProductoCodigo()));

                    // El precio unitario se captura en COP como snapshot inmutable
                    Dinero precioSnapshot = new Dinero(item.getPrecioUnitario(), Moneda.COP);

                    return new OrdenProducto(
                            item.getProductoCodigo(),
                            item.getCantidad(),
                            precioSnapshot,
                            producto
                    );
                })
                .toList();

        // Calcular total sumando subtotales (precio * cantidad por línea)
        BigDecimal totalCop = lineas.stream()
                .map(op -> op.calcularSubtotal().getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String numeroOrden = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Orden orden = new Orden(
                null,                   // id — aún no persistido
                numeroOrden,
                request.getClienteId(),
                null,                   // cliente — referencia no necesaria en creación
                ZonedDateTime.now(),
                EstadoOrden.PENDIENTE,
                totalCop,
                lineas,
                null,                   // creadoEn — asignado por la BD
                null                    // actualizadoEn — asignado por la BD
        );

        Orden guardada = ordenRepositoryPort.guardar(orden);
        log.info("Orden creada con número: {}", guardada.getNumeroOrden());
        return toResponse(guardada);
    }

    // -------------------------------------------------------------------------
    // Mapeo privado dominio → DTO
    // -------------------------------------------------------------------------

    private OrdenResponse toResponse(Orden o) {
        List<OrdenResponse.OrdenProductoResponse> lineas = o.getProductos().stream()
                .map(op -> new OrdenResponse.OrdenProductoResponse(
                        op.getProductoCodigo(),
                        op.getProducto() != null ? op.getProducto().getNombre() : null,
                        op.getCantidad(),
                        op.getPrecioUnitario().getMonto(),
                        op.calcularSubtotal().getMonto()
                ))
                .toList();

        return new OrdenResponse(
                o.getId(),
                o.getNumeroOrden(),
                o.getClienteId(),
                o.getCliente() != null ? o.getCliente().getNombre() : null,
                o.getFechaOrden(),
                o.getEstado(),
                o.getTotalCop(),
                lineas,
                o.getCreadoEn()
        );
    }
}
