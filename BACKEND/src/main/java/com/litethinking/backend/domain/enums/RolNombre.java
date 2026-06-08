package com.litethinking.backend.domain.enums;

/**
 * Roles disponibles en el sistema LiteThinking.
 *
 * <p>Clean Architecture — Capa de Dominio: enum 100 % Java puro, sin ninguna
 * dependencia de framework externo. Cada constante porta su propia semántica
 * de negocio a través de los campos {@code etiqueta} y {@code descripcion}.</p>
 *
 * <ul>
 *   <li>{@link #ADMIN}   – Administrador con acceso total al sistema.</li>
 *   <li>{@link #EXTERNO} – Usuario externo con acceso restringido.</li>
 * </ul>
 */
public enum RolNombre {

    /**
     * Administrador del sistema.
     * Tiene permisos completos sobre todos los recursos:
     * CRUD de empresas, productos, usuarios, inventario y órdenes.
     */
    ADMIN(
            "Administrador",
            "Acceso total al sistema: gestión de empresas, productos, usuarios, inventario y órdenes."
    ),

    /**
     * Usuario externo (cliente o integrador).
     * Acceso restringido: puede consultar el catálogo de productos
     * y gestionar únicamente sus propias órdenes.
     */
    EXTERNO(
            "Usuario Externo",
            "Acceso restringido: consulta de catálogo y gestión de órdenes propias."
    );

    // -------------------------------------------------------------------------
    // Estado
    // -------------------------------------------------------------------------

    private final String etiqueta;
    private final String descripcion;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    RolNombre(String etiqueta, String descripcion) {
        this.etiqueta    = etiqueta;
        this.descripcion = descripcion;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * Devuelve el nombre legible del rol para presentación en UI o logs.
     *
     * @return etiqueta del rol, nunca {@code null}.
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Devuelve la descripción completa de los permisos asociados al rol.
     *
     * @return descripción del rol, nunca {@code null}.
     */
    public String getDescripcion() {
        return descripcion;
    }

    // -------------------------------------------------------------------------
    // Métodos de negocio
    // -------------------------------------------------------------------------

    /**
     * Indica si este rol posee privilegios de administración total.
     *
     * @return {@code true} únicamente cuando el rol es {@link #ADMIN}.
     */
    public boolean esAdministrador() {
        return this == ADMIN;
    }
}
