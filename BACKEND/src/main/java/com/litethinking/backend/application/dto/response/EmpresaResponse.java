package com.litethinking.backend.application.dto.response;

import java.time.ZonedDateTime;

/**
 * DTO de respuesta para la entidad {@code Empresa}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * de una empresa desde el caso de uso hacia el adaptador de salida (controlador REST).
 * Nunca expone la contraseña ni datos sensibles de infraestructura.</p>
 *
 * <p><strong>Campos:</strong></p>
 * <ul>
 *   <li>{@code nit}: NIT de la empresa en formato colombiano (ej.: "900123456-1").</li>
 *   <li>{@code nombre}: razón social de la empresa.</li>
 *   <li>{@code direccion}: dirección fiscal de la empresa.</li>
 *   <li>{@code telefono}: número de contacto, puede ser {@code null}.</li>
 *   <li>{@code activa}: estado operativo de la empresa.</li>
 *   <li>{@code creadoEn}: timestamp de creación con zona horaria.</li>
 *   <li>{@code actualizadoEn}: timestamp de última actualización con zona horaria.</li>
 * </ul>
 */
public record EmpresaResponse(
        String nit,
        String nombre,
        String direccion,
        String telefono,
        boolean activa,
        ZonedDateTime creadoEn,
        ZonedDateTime actualizadoEn
) {

    /**
     * Factory method estático para construir un {@code EmpresaResponse}
     * directamente desde la entidad de dominio {@link com.litethinking.backend.domain.model.Empresa}.
     *
     * @param empresa entidad de dominio fuente; nunca {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     */
    public static EmpresaResponse desde(com.litethinking.backend.domain.model.Empresa empresa) {
        return new EmpresaResponse(
                empresa.getNitValor(),
                empresa.getNombre(),
                empresa.getDireccion(),
                empresa.getTelefono(),
                empresa.isActiva(),
                empresa.getCreadoEn(),
                empresa.getActualizadoEn()
        );
    }
}
