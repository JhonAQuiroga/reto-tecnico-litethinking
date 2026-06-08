package com.litethinking.backend.domain.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados posibles de una Orden de compra y sus reglas de transición.
 *
 * <p>Clean Architecture — Capa de Dominio: este enum encapsula la máquina de estados
 * del ciclo de vida de una Orden. Cada constante conoce las transiciones que le están
 * permitidas, evitando que esa lógica se disperse por la capa de aplicación.</p>
 *
 * <p>Diagrama de transiciones válidas:</p>
 * <pre>
 *   PENDIENTE ──► PROCESADA
 *   PENDIENTE ──► CANCELADA
 *   PROCESADA ──► (estado terminal, sin transiciones)
 *   CANCELADA ──► (estado terminal, sin transiciones)
 * </pre>
 */
public enum EstadoOrden {

    /**
     * La orden fue creada pero aún no ha sido confirmada ni procesada.
     * Desde este estado se puede avanzar a {@link #PROCESADA} o retroceder a {@link #CANCELADA}.
     */
    PENDIENTE("Pendiente", "Orden creada y en espera de procesamiento.") {
        @Override
        public Set<EstadoOrden> transicionesPermitidas() {
            return EnumSet.of(PROCESADA, CANCELADA);
        }
    },

    /**
     * La orden fue confirmada y ejecutada exitosamente.
     * Estado terminal: no admite más transiciones.
     */
    PROCESADA("Procesada", "Orden confirmada y ejecutada exitosamente.") {
        @Override
        public Set<EstadoOrden> transicionesPermitidas() {
            return EnumSet.noneOf(EstadoOrden.class);
        }
    },

    /**
     * La orden fue cancelada antes de ser procesada.
     * Estado terminal: no admite más transiciones.
     */
    CANCELADA("Cancelada", "Orden cancelada antes de su procesamiento.") {
        @Override
        public Set<EstadoOrden> transicionesPermitidas() {
            return EnumSet.noneOf(EstadoOrden.class);
        }
    };

    // -------------------------------------------------------------------------
    // Estado
    // -------------------------------------------------------------------------

    private final String etiqueta;
    private final String descripcion;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    EstadoOrden(String etiqueta, String descripcion) {
        this.etiqueta    = etiqueta;
        this.descripcion = descripcion;
    }

    // -------------------------------------------------------------------------
    // Métodos de máquina de estados (abstract: cada constante los implementa)
    // -------------------------------------------------------------------------

    /**
     * Devuelve el conjunto de estados hacia los cuales esta constante puede transicionar.
     *
     * @return conjunto inmutable de transiciones permitidas; vacío si es estado terminal.
     */
    public abstract Set<EstadoOrden> transicionesPermitidas();

    /**
     * Determina si la transición hacia {@code nuevoEstado} es válida según las
     * reglas de negocio del ciclo de vida de la Orden.
     *
     * @param nuevoEstado estado destino al que se desea transicionar.
     * @return {@code true} si la transición está permitida, {@code false} en caso contrario.
     */
    public boolean puedeTransicionarA(EstadoOrden nuevoEstado) {
        return transicionesPermitidas().contains(nuevoEstado);
    }

    /**
     * Indica si este estado es terminal (no admite más transiciones).
     *
     * @return {@code true} si el estado no tiene transiciones posibles.
     */
    public boolean esTerminal() {
        return transicionesPermitidas().isEmpty();
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * @return etiqueta legible del estado, nunca {@code null}.
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * @return descripción del estado, nunca {@code null}.
     */
    public String getDescripcion() {
        return descripcion;
    }
}
