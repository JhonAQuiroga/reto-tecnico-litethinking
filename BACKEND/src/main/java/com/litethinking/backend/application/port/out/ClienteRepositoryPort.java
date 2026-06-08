package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    List<Cliente> listarTodos();

    Optional<Cliente> buscarPorId(Long id);

    boolean existePorEmail(String email);

    Cliente guardar(Cliente cliente);

    void eliminar(Long id);
}
