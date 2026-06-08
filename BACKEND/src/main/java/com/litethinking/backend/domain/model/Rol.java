package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.enums.RolNombre;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Rol de usuario.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo. La lógica de negocio
 * asociada al rol vive aquí, no en la capa de infraestructura.</p>
 *
 * <p>Un {@code Rol} otorga un conjunto de permisos a un {@link Usuario}.
 * Los permisos concretos están modelados en {@link RolNombre}.</p>
 */
public final class Rol {

    private final Long          id;
    private final RolNombre     nombre;
    private final String        descripcion;
    private final ZonedDateTime creadoEn;
    private final ZonedDateTime actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Rol}.
     *
     * @param id            identificador de base de datos (puede ser {@code null}
     *                      para entidades aún no persistidas).
     * @param nombre        nombre del rol; nunca {@code null}.
     * @param descripcion   descripción legible del rol; puede ser {@code null}.
     * @param creadoEn      timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException si {@code nombre} es {@code null}.
     */
    public Rol(Long id,
               RolNombre nombre,
               String descripcion,
               ZonedDateTime creadoEn,
               ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(nombre, "El nombre del rol no puede ser nulo.");

        this.id            = id;
        this.nombre        = nombre;
        this.descripcion   = descripcion;
        this.creadoEn      = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public RolNombre getNombre() {
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
    // Métodos de negocio
    // -------------------------------------------------------------------------

    /**
     * Indica si este rol otorga privilegios de administración total.
     *
     * @return {@code true} si el rol es {@link RolNombre#ADMIN}.
     */
    public boolean esAdministrador() {
        return RolNombre.ADMIN.equals(this.nombre);
    }

    // -------------------------------------------------------------------------
    // Igualdad por identidad de negocio, hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de un Rol está determinada por su {@link RolNombre}. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rol other)) return false;
        return nombre == other.nombre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return "Rol{nombre=" + nombre + ", descripcion='" + descripcion + "'}";
    }
}
