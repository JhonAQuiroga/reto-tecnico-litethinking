package com.litethinking.backend.domain.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Cliente.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>Un {@code Cliente} puede generar múltiples {@link Orden}es (One-to-Many).
 * Su identidad de negocio está determinada por el email, que debe ser único
 * en el sistema.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El nombre y el email son obligatorios.</li>
 *   <li>El email se normaliza a minúsculas para garantizar unicidad sin distinción de caja.</li>
 *   <li>Un cliente inactivo no puede generar nuevas órdenes.</li>
 * </ul>
 */
public final class Cliente {

    private final Long          id;
    private final String        nombre;
    private final String        email;
    private final String        telefono;
    private final String        direccion;
    private final boolean       activo;
    private final ZonedDateTime creadoEn;
    private final ZonedDateTime actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Cliente}.
     *
     * @param id            identificador de base de datos ({@code null} si aún no persistido).
     * @param nombre        nombre completo del cliente; nunca {@code null} ni en blanco.
     * @param email         correo electrónico único; nunca {@code null} ni en blanco.
     * @param telefono      número de contacto; puede ser {@code null}.
     * @param direccion     dirección de entrega; puede ser {@code null}.
     * @param activo        {@code true} si el cliente puede generar órdenes.
     * @param creadoEn      timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException     si {@code nombre} o {@code email} son {@code null}.
     * @throws IllegalArgumentException si {@code nombre} o {@code email} están en blanco.
     */
    public Cliente(Long id,
                   String nombre,
                   String email,
                   String telefono,
                   String direccion,
                   boolean activo,
                   ZonedDateTime creadoEn,
                   ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(nombre, "El nombre del cliente no puede ser nulo.");
        Objects.requireNonNull(email,  "El email del cliente no puede ser nulo.");

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar en blanco.");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("El email del cliente no puede estar en blanco.");
        }

        this.id            = id;
        this.nombre        = nombre.trim();
        this.email         = email.trim().toLowerCase();
        this.telefono      = (telefono  != null) ? telefono.trim()  : null;
        this.direccion     = (direccion != null) ? direccion.trim() : null;
        this.activo        = activo;
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

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isActivo() {
        return activo;
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
     * Indica si el cliente puede emitir nuevas órdenes de compra.
     *
     * @return {@code true} si el cliente está activo en el sistema.
     */
    public boolean puedeOrdenar() {
        return this.activo;
    }

    // -------------------------------------------------------------------------
    // Igualdad por email (identidad de negocio), hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de un {@code Cliente} está determinada por su email único. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente other)) return false;
        return email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', email='" + email +
               "', activo=" + activo + "}";
    }
}
