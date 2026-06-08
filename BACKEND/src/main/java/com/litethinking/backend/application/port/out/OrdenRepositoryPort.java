package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenRepositoryPort {

    List<Orden> listarTodas();

    List<Orden> listarPorCliente(Long clienteId);

    Optional<Orden> buscarPorId(Long id);

    Optional<Orden> buscarPorNumeroOrden(String numeroOrden);

    Orden guardar(Orden orden);
}
