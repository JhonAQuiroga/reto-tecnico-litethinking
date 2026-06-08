package com.litethinking.backend.domain.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Categoría de Producto.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>Una {@code Categoria} es una etiqueta organizacional que se asocia a
 * uno o más {@link Producto}s en una relación Many-to-Many. Esta entidad
 * encapsula su propia invariante: el nombre es obligatorio y único en el sistema.</p>
 */
public final class Categoria {

    private final Long          id;
    private final String        nombre;
    private final String        descripcion;
    private final ZonedDateTime creadoEn;
    private final ZonedDateTime actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Categoria}.
     *
     * @param id            identificador de base de datos ({@code null} si aún no persistido).
     * @param nombre        nombre único de la categoría; nunca {@code null} ni en blanco.
     * @param descripcion   descripción opcional de la categoría; puede ser {@code null}.
     * @param creadoEn      timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException     si {@code nombre} es {@code null}.
     * @throws IllegalArgumentException si {@code nombre} está en blanco.
     */
    public Categoria(Long id,
                     String nombre,
                     String descripcion,
                     ZonedDateTime creadoEn,
                     ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(nombre, "El nombre de la categoría no puede ser nulo.");

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar en blanco.");
        }

        this.id            = id;
        this.nombre        = nombre.trim();
        this.descripcion   = (descripcion != null) ? descripcion.trim() : null;
        this.creadoEn      = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ZonedDateTime getCreadoEn() {
        return creadoEn;
    }

    public ZonedDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Igualdad por nombre (identidad de negocio), hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de una {@code Categoria} está determinada por su nombre. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria other)) return false;
        return nombre.equalsIgnoreCase(other.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre='" + nombre + "'}";
    }
}
