package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.enums.RolNombre;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Usuario del sistema.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El nombre de usuario, email, contraseña y rol son obligatorios.</li>
 *   <li>La contraseña se almacena <em>exclusivamente</em> como hash BCrypt;
 *       nunca se persiste ni se expone en texto plano.</li>
 *   <li>Un usuario inactivo ({@code activo = false}) no puede autenticarse
 *       (esta regla se aplica en el caso de uso de autenticación).</li>
 * </ul>
 */
public final class Usuario {

    private final Long          id;
    private final String        nombre;
    private final String        email;
    /** Hash BCrypt de la contraseña. Nunca texto plano. */
    private final String        passwordHash;
    private final boolean       activo;
    private final Rol           rol;
    private final ZonedDateTime creadoEn;
    private final ZonedDateTime actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Usuario}.
     *
     * @param id           identificador de base de datos ({@code null} si aún no persistido).
     * @param nombre       nombre completo del usuario; nunca {@code null} ni en blanco.
     * @param email        correo electrónico único del usuario; nunca {@code null} ni en blanco.
     * @param passwordHash hash BCrypt de la contraseña; nunca {@code null} ni en blanco.
     * @param activo       {@code true} si el usuario puede autenticarse.
     * @param rol          rol asignado al usuario; nunca {@code null}.
     * @param creadoEn     timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException     si {@code nombre}, {@code email}, {@code passwordHash}
     *                                  o {@code rol} son {@code null}.
     * @throws IllegalArgumentException si {@code nombre}, {@code email} o {@code passwordHash}
     *                                  están en blanco.
     */
    public Usuario(Long id,
                   String nombre,
                   String email,
                   String passwordHash,
                   boolean activo,
                   Rol rol,
                   ZonedDateTime creadoEn,
                   ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(nombre,       "El nombre del usuario no puede ser nulo.");
        Objects.requireNonNull(email,        "El email del usuario no puede ser nulo.");
        Objects.requireNonNull(passwordHash, "El hash de contraseña no puede ser nulo.");
        Objects.requireNonNull(rol,          "El rol del usuario no puede ser nulo.");

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar en blanco.");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("El email del usuario no puede estar en blanco.");
        }
        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("El hash de contraseña no puede estar en blanco.");
        }

        this.id            = id;
        this.nombre        = nombre.trim();
        this.email         = email.trim().toLowerCase();
        this.passwordHash  = passwordHash;
        this.activo        = activo;
        this.rol           = rol;
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

    /**
     * Devuelve el hash BCrypt de la contraseña.
     *
     * <p><strong>Nunca exponer este valor directamente en respuestas de API.</strong>
     * Su uso está restringido a la capa de infraestructura de autenticación.</p>
     *
     * @return hash BCrypt, nunca {@code null}.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public Rol getRol() {
        return rol;
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
     * Indica si el usuario tiene rol de administrador.
     *
     * @return {@code true} si el rol del usuario es {@link RolNombre#ADMIN}.
     */
    public boolean esAdministrador() {
        return rol.esAdministrador();
    }

    /**
     * Indica si el usuario puede autenticarse en el sistema.
     * Un usuario debe estar activo para poder iniciar sesión.
     *
     * @return {@code true} si el usuario está activo.
     */
    public boolean puedeAutenticarse() {
        return this.activo;
    }

    // -------------------------------------------------------------------------
    // Igualdad por email (identidad de negocio), hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de un {@code Usuario} está determinada por su email único. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario other)) return false;
        return email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email +
               "', rol=" + rol.getNombre() + ", activo=" + activo + "}";
    }
}
