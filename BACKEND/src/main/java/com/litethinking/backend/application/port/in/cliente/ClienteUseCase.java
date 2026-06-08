package com.litethinking.backend.application.port.in.cliente;

import com.litethinking.backend.application.dto.request.ClienteRequest;
import com.litethinking.backend.application.dto.response.ClienteResponse;

import java.util.List;

public interface ClienteUseCase {
    List<ClienteResponse> listarTodos();
    ClienteResponse buscarPorId(Long id);
    ClienteResponse crear(ClienteRequest request);
    ClienteResponse actualizar(Long id, ClienteRequest request);
    void eliminar(Long id);
}
