package com.litethinking.backend.application.port.in.empresa;

import com.litethinking.backend.application.dto.response.EmpresaResponse;

import java.util.List;

/**
 * Puerto de entrada: casos de uso para <strong>consultar empresas</strong>.
 *
 * <p>Clean Architecture — Application Layer: agrupa todas las operaciones de lectura
 * sobre la entidad Empresa. Al ser operaciones de consulta pura (sin efectos secundarios),
 * se ejecutan en transacciones de solo lectura para optimizar el rendimiento.</p>
 *
 * <p><strong>Operaciones disponibles:</strong></p>
 * <ul>
 *   <li>Listar todas las empresas registradas.</li>
 *   <li>Buscar una empresa específica por su NIT.</li>
 * </ul>
 */
public interface ConsultarEmpresaUseCase {

    /**
     * Recupera todas las empresas registradas en el sistema.
     *
     * @return lista de {@link EmpresaResponse} con los datos de todas las empresas;
     *         nunca {@code null}, puede estar vacía si no hay empresas registradas.
     */
    List<EmpresaResponse> listarTodas();

    /**
     * Busca y recupera una empresa por su NIT.
     *
     * @param nit valor textual del NIT a buscar (ej.: "900123456-1"); nunca {@code null}.
     * @return {@link EmpresaResponse} con los datos de la empresa encontrada; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si no existe una empresa con el NIT indicado.
     */
    EmpresaResponse buscarPorNit(String nit);
}
