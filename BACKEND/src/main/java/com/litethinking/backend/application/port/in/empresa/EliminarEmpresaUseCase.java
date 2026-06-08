package com.litethinking.backend.application.port.in.empresa;

/**
 * Puerto de entrada: caso de uso para <strong>eliminar una empresa</strong>.
 *
 * <p>Clean Architecture — Application Layer: orquesta la eliminación de una empresa
 * a partir de su NIT. Dependiendo de la estrategia de infraestructura configurada,
 * puede ser una eliminación física o lógica (marcando {@code activa = false}).</p>
 *
 * <p><strong>Reglas de negocio aplicadas:</strong></p>
 * <ul>
 *   <li>La empresa debe existir antes de intentar eliminarla.</li>
 *   <li>Si existen órdenes activas asociadas, la eliminación puede rechazarse
 *       dependiendo de la política de negocio configurada.</li>
 * </ul>
 */
public interface EliminarEmpresaUseCase {

    /**
     * Ejecuta el caso de uso de eliminación de empresa.
     *
     * @param nit valor textual del NIT de la empresa a eliminar; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si no existe una empresa con el NIT indicado.
     */
    void ejecutar(String nit);
}
