package com.litethinking.backend.application.dto.response;

import java.time.ZonedDateTime;

/**
 * DTO de respuesta para la entidad {@code Cliente}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * de un cliente desde el caso de uso hacia el adaptador de salida (controlador REST).
 * No expone el hash de contraseña ni datos sensibles.</p>
 */
public record ClienteResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        boolean activo,
        ZonedDateTime creadoEn,
        ZonedDateTime actualizadoEn
) {

    /**
     * Factory method estático para construir un {@code ClienteResponse}
     * directamente desde la entidad de dominio {@link com.litethinking.backend.domain.model.Cliente}.
     *
     * @param cliente entidad de dominio fuente; nunca {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     */
    public static ClienteResponse desde(com.litethinking.backend.domain.model.Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.isActivo(),
                cliente.getCreadoEn(),
                cliente.getActualizadoEn()
        );
    }
}
