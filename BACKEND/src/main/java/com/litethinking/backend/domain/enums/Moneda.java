package com.litethinking.backend.domain.enums;

/**
 * Monedas soportadas por el sistema para expresar precios multimoneda de productos.
 *
 * <p>Clean Architecture — Capa de Dominio: cada constante porta su símbolo monetario,
 * nombre oficial y precisión decimal, eliminando la necesidad de tablas de configuración
 * o constantes dispersas en la capa de infraestructura.</p>
 *
 * <p>Las tres monedas corresponden directamente a las columnas
 * {@code precio_cop}, {@code precio_usd} y {@code precio_eur}
 * de la tabla {@code litethinking.productos}.</p>
 */
public enum Moneda {

    /**
     * Peso colombiano — moneda base del sistema.
     * Código ISO 4217: COP.
     */
    COP("$", "Peso colombiano", "COP", 2),

    /**
     * Dólar estadounidense.
     * Código ISO 4217: USD.
     */
    USD("US$", "Dólar estadounidense", "USD", 2),

    /**
     * Euro.
     * Código ISO 4217: EUR.
     */
    EUR("€", "Euro", "EUR", 2);

    // -------------------------------------------------------------------------
    // Estado
    // -------------------------------------------------------------------------

    /** Símbolo tipográfico de la moneda (ej.: "$", "US$", "€"). */
    private final String simbolo;

    /** Nombre oficial completo de la moneda. */
    private final String descripcion;

    /** Código ISO 4217 de tres letras. */
    private final String codigoIso;

    /** Número de decimales admitidos para representar montos en esta moneda. */
    private final int decimales;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    Moneda(String simbolo, String descripcion, String codigoIso, int decimales) {
        this.simbolo     = simbolo;
        this.descripcion = descripcion;
        this.codigoIso   = codigoIso;
        this.decimales   = decimales;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * @return símbolo tipográfico de la moneda, nunca {@code null}.
     */
    public String getSimbolo() {
        return simbolo;
    }

    /**
     * @return nombre oficial de la moneda, nunca {@code null}.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return código ISO 4217 de la moneda (ej.: "COP", "USD", "EUR").
     */
    public String getCodigoIso() {
        return codigoIso;
    }

    /**
     * @return número de decimales para representar importes en esta moneda.
     */
    public int getDecimales() {
        return decimales;
    }

    // -------------------------------------------------------------------------
    // Métodos de negocio
    // -------------------------------------------------------------------------

    /**
     * Formatea un texto de monto añadiéndole el símbolo de esta moneda.
     * Útil para representación en logs y mensajes de dominio.
     *
     * @param monto cadena que representa el monto (ej.: "19999.00").
     * @return cadena formateada (ej.: "$ 19999.00", "US$ 100.00", "€ 85.00").
     */
    public String formatear(String monto) {
        return simbolo + " " + monto;
    }
}
