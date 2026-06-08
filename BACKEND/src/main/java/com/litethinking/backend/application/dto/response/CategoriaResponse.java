package com.litethinking.backend.application.dto.response;

/**
 * DTO de respuesta para la entidad {@code Categoria}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21. Se incluye como dato
 * anidado dentro de {@link ProductoResponse} para exponer las categorías asociadas
 * a un producto sin exponer los detalles internos de la entidad de dominio.</p>
 */
public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion
) {

    /**
     * Factory method estático para construir un {@code CategoriaResponse}
     * directamente desde la entidad de dominio {@link com.litethinking.backend.domain.model.Categoria}.
     *
     * @param categoria entidad de dominio fuente; nunca {@code null}.
     * @return DTO de respuesta poblado; nunca {@code null}.
     */
    public static CategoriaResponse desde(com.litethinking.backend.domain.model.Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
