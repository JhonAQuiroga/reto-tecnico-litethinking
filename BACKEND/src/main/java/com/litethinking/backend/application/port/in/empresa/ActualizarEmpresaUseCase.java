package com.litethinking.backend.application.port.in.empresa;

import com.litethinking.backend.application.dto.request.EmpresaRequest;
import com.litethinking.backend.application.dto.response.EmpresaResponse;

/**
 * Puerto de entrada: caso de uso para <strong>actualizar los datos de una empresa existente</strong>.
 *
 * <p>Clean Architecture — Application Layer: permite modificar el nombre, dirección y teléfono
 * de una empresa ya registrada. El NIT, como clave natural de negocio, es inmutable.</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>La empresa debe existir; de lo contrario se lanza una excepción.</li>
 *   <li>El NIT no puede ser modificado (es la identidad de negocio).</li>
 * </ul>
 */
public interface ActualizarEmpresaUseCase {

    /**
     * Ejecuta el caso de uso de actualización de empresa.
     *
     * @param nit     NIT de la empresa a actualizar (identificador de la ruta); nunca {@code null}.
     * @param request DTO con los nuevos datos a persistir; nunca {@code null}.
     * @return {@link EmpresaResponse} con los datos actualizados; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si no existe una empresa con el NIT indicado.
     */
    EmpresaResponse ejecutar(String nit, EmpresaRequest request);
}
