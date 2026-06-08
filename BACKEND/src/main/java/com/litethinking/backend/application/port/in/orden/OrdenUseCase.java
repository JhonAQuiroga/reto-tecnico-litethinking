package com.litethinking.backend.application.port.in.orden;

import com.litethinking.backend.application.dto.request.OrdenRequest;
import com.litethinking.backend.application.dto.response.OrdenResponse;

import java.util.List;

public interface OrdenUseCase {
    List<OrdenResponse> listarTodas();
    List<OrdenResponse> listarPorCliente(Long clienteId);
    OrdenResponse buscarPorId(Long id);
    OrdenResponse crear(OrdenRequest request);
}
