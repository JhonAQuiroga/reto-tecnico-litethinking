package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.valueobject.NitEmpresa;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entidad de dominio: Empresa.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>El NIT es la <strong>clave natural de negocio</strong> (PK lógica) y se modela
 * como el Value Object {@link NitEmpresa}, garantizando su validez en todo momento.
 * Una empresa puede tener múltiples {@link Producto}s asociados a través del inventario.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El NIT, nombre y dirección son obligatorios.</li>
 *   <li>Una empresa inactiva no puede recibir nuevas órdenes de compra
 *       (regla aplicada en el caso de uso correspondiente).</li>
 * </ul>
 */
public final class Empresa {

    private final NitEmpresa    nit;
    private final String        nombre;
    private final String        direccion;
    private final String        telefono;
    private final boolean       activa;
    private final ZonedDateTime creadoEn;
    private final ZonedDateTime actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Empresa}.
     *
     * @param nit           NIT de la empresa como Value Object; nunca {@code null}.
     * @param nombre        razón social de la empresa; nunca {@code null} ni en blanco.
     * @param direccion     dirección fiscal; nunca {@code null} ni en blanco.
     * @param telefono      número de contacto; puede ser {@code null}.
     * @param activa        {@code true} si la empresa opera activamente.
     * @param creadoEn      timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException     si {@code nit}, {@code nombre} o {@code direccion}
     *                                  son {@code null}.
     * @throws IllegalArgumentException si {@code nombre} o {@code direccion} están en blanco.
     */
    public Empresa(NitEmpresa nit,
                   String nombre,
                   String direccion,
                   String telefono,
                   boolean activa,
                   ZonedDateTime creadoEn,
                   ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(nit,      "El NIT de la empresa no puede ser nulo.");
        Objects.requireNonNull(nombre,   "El nombre de la empresa no puede ser nulo.");
        Objects.requireNonNull(direccion,"La dirección de la empresa no puede ser nula.");

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar en blanco.");
        }
        if (direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección de la empresa no puede estar en blanco.");
        }

        this.nit           = nit;
        this.nombre        = nombre.trim();
        this.direccion     = direccion.trim();
        this.telefono      = (telefono != null) ? telefono.trim() : null;
        this.activa        = activa;
        this.creadoEn      = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * @return NIT de la empresa como Value Object; nunca {@code null}.
     */
    public NitEmpresa getNit() {
        return nit;
    }

    /**
     * Conveniencia: devuelve el valor String del NIT sin necesidad de navegar al VO.
     * Útil para consultas de infraestructura.
     *
     * @return representación textual del NIT (ej.: "900123456-1").
     */
    public String getNitValor() {
        return nit.getValor();
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isActiva() {
        return activa;
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
     * Indica si la empresa puede participar en nuevas transacciones comerciales.
     *
     * @return {@code true} si la empresa está activa.
     */
    public boolean puedeOperar() {
        return this.activa;
    }

    // -------------------------------------------------------------------------
    // Igualdad por NIT (identidad de negocio), hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de una {@code Empresa} está determinada por su NIT. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa other)) return false;
        return nit.equals(other.nit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nit);
    }

    @Override
    public String toString() {
        return "Empresa{nit=" + nit + ", nombre='" + nombre + "', activa=" + activa + "}";
    }
}
