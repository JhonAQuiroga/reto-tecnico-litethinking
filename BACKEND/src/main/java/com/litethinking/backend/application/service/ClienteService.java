package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.ClienteRequest;
import com.litethinking.backend.application.dto.response.ClienteResponse;
import com.litethinking.backend.application.port.in.cliente.ClienteUseCase;
import com.litethinking.backend.application.port.out.ClienteRepositoryPort;
import com.litethinking.backend.domain.exception.ClienteNotFoundException;
import com.litethinking.backend.domain.exception.RecursoYaExisteException;
import com.litethinking.backend.domain.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Servicio de aplicación para el CRUD de Clientes.
 *
 * <p>Clean Architecture — Application Layer: orquesta los casos de uso relacionados
 * con la entidad {@link Cliente}. Usa el constructor explícito del dominio puro
 * (sin Lombok), respetando sus invariantes de negocio.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteRepositoryPort.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return toResponse(clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNotFoundException(id)));
    }

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepositoryPort.existePorEmail(request.getEmail())) {
            throw new RecursoYaExisteException("Cliente", request.getEmail());
        }
        // Construcción con constructor explícito del dominio puro (sin Lombok)
        Cliente cliente = new Cliente(
                null,                   // id — aún no persistido
                request.getNombre(),
                request.getEmail(),
                request.getTelefono(),
                request.getDireccion(),
                true,                   // activo por defecto
                null,                   // creadoEn — asignado por la BD
                null                    // actualizadoEn — asignado por la BD
        );
        Cliente guardado = clienteRepositoryPort.guardar(cliente);
        log.info("Cliente creado con email: {}", guardado.getEmail());
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente existente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        // Re-construir con datos actualizados manteniendo el email original (identidad de negocio)
        Cliente actualizado = new Cliente(
                existente.getId(),
                request.getNombre(),
                existente.getEmail(),       // el email no cambia (es la clave natural)
                request.getTelefono(),
                request.getDireccion(),
                existente.isActivo(),
                existente.getCreadoEn(),    // preservar timestamp original
                ZonedDateTime.now()
        );
        Cliente guardado = clienteRepositoryPort.guardar(actualizado);
        log.info("Cliente actualizado con id: {}", id);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (clienteRepositoryPort.buscarPorId(id).isEmpty()) {
            throw new ClienteNotFoundException(id);
        }
        clienteRepositoryPort.eliminar(id);
        log.info("Cliente eliminado con id: {}", id);
    }

    // -------------------------------------------------------------------------
    // Mapeo privado dominio → DTO
    // -------------------------------------------------------------------------

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getNombre(),
                c.getEmail(),
                c.getTelefono(),
                c.getDireccion(),
                c.isActivo(),
                c.getCreadoEn(),
                c.getActualizadoEn()
        );
    }
}
