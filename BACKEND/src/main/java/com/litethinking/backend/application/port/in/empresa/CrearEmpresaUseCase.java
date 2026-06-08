package com.litethinking.backend.application.port.in.empresa;

import com.litethinking.backend.application.dto.request.EmpresaRequest;
import com.litethinking.backend.application.dto.response.EmpresaResponse;

/**
 * Puerto de entrada: caso de uso para <strong>crear una nueva empresa</strong>.
 *
 * <p>Clean Architecture — Application Layer: esta interfaz es el contrato que expone
 * el caso de uso hacia los adaptadores de entrada (controladores REST). La implementación
 * concreta es {@code EmpresaService}.</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>El NIT de la empresa debe ser único en el sistema.</li>
 *   <li>La empresa se crea con estado {@code activa = true} por defecto.</li>
 *   <li>El NIT debe cumplir el formato colombiano: {@code [6-10 dígitos]-[1 dígito]}.</li>
 * </ul>
 */
public interface CrearEmpresaUseCase {

    /**
     * Ejecuta el caso de uso de creación de empresa.
     *
     * @param request DTO con los datos validados de la empresa a crear; nunca {@code null}.
     * @return {@link EmpresaResponse} con los datos de la empresa recién creada,
     *         incluyendo timestamps de auditoría; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.RecursoYaExisteException
     *         si ya existe una empresa con el mismo NIT.
     */
    EmpresaResponse ejecutar(EmpresaRequest request);
}
