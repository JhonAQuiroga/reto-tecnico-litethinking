package com.litethinking.backend.domain.model;

import com.litethinking.backend.domain.enums.Moneda;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.valueobject.NitEmpresa;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio: Producto.
 *
 * <p>Clean Architecture — Capa de Dominio: POJO puro, sin ninguna anotación
 * de Spring, JPA o cualquier otro framework externo.</p>
 *
 * <p>El código del producto es su <strong>clave natural de negocio</strong> (PK lógica)
 * y se modela como el Value Object {@link CodigoProducto}.</p>
 *
 * <p>Los precios multimoneda se modelan como instancias de {@link Dinero},
 * cada una con su {@link Moneda} embebida, lo que garantiza coherencia aritmética
 * y elimina ambigüedad sobre la divisa de un monto concreto.</p>
 *
 * <p>La relación Many-to-Many con {@link Categoria} se resuelve como una lista
 * inmutable dentro del agregado de dominio.</p>
 *
 * <p><strong>Invariantes de negocio:</strong></p>
 * <ul>
 *   <li>El código, nombre y NIT de empresa son obligatorios.</li>
 *   <li>Los tres precios (COP, USD, EUR) son obligatorios y no pueden ser negativos
 *       (validación delegada al VO {@link Dinero}).</li>
 *   <li>Un producto inactivo no debe aparecer en el catálogo ni en nuevas órdenes.</li>
 * </ul>
 */
public final class Producto {

    private final CodigoProducto  codigo;
    private final String          nombre;
    private final String          caracteristicas;
    private final Dinero          precioCop;
    private final Dinero          precioUsd;
    private final Dinero          precioEur;
    private final NitEmpresa      empresaNit;
    private final boolean         activo;
    private final List<Categoria> categorias;
    private final ZonedDateTime   creadoEn;
    private final ZonedDateTime   actualizadoEn;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code Producto}.
     *
     * @param codigo          código único del producto como VO; nunca {@code null}.
     * @param nombre          nombre comercial del producto; nunca {@code null} ni en blanco.
     * @param caracteristicas descripción técnica del producto; puede ser {@code null}.
     * @param precioCop       precio en pesos colombianos como VO; nunca {@code null}.
     * @param precioUsd       precio en dólares estadounidenses como VO; nunca {@code null}.
     * @param precioEur       precio en euros como VO; nunca {@code null}.
     * @param empresaNit      NIT de la empresa propietaria del producto; nunca {@code null}.
     * @param activo          {@code true} si el producto está disponible en el catálogo.
     * @param categorias      lista de categorías asociadas; puede ser {@code null} o vacía.
     * @param creadoEn        timestamp de creación; puede ser {@code null}.
     * @param actualizadoEn   timestamp de última actualización; puede ser {@code null}.
     * @throws NullPointerException     si alguno de los parámetros obligatorios es {@code null}.
     * @throws IllegalArgumentException si el nombre está en blanco, o si algún precio
     *                                  no corresponde a la moneda esperada.
     */
    public Producto(CodigoProducto codigo,
                    String nombre,
                    String caracteristicas,
                    Dinero precioCop,
                    Dinero precioUsd,
                    Dinero precioEur,
                    NitEmpresa empresaNit,
                    boolean activo,
                    List<Categoria> categorias,
                    ZonedDateTime creadoEn,
                    ZonedDateTime actualizadoEn) {

        Objects.requireNonNull(codigo,     "El código del producto no puede ser nulo.");
        Objects.requireNonNull(nombre,     "El nombre del producto no puede ser nulo.");
        Objects.requireNonNull(precioCop,  "El precio en COP no puede ser nulo.");
        Objects.requireNonNull(precioUsd,  "El precio en USD no puede ser nulo.");
        Objects.requireNonNull(precioEur,  "El precio en EUR no puede ser nulo.");
        Objects.requireNonNull(empresaNit, "El NIT de la empresa propietaria no puede ser nulo.");

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar en blanco.");
        }
        validarMonedaDeprecio(precioCop, Moneda.COP, "COP");
        validarMonedaDeprecio(precioUsd, Moneda.USD, "USD");
        validarMonedaDeprecio(precioEur, Moneda.EUR, "EUR");

        this.codigo          = codigo;
        this.nombre          = nombre.trim();
        this.caracteristicas = (caracteristicas != null) ? caracteristicas.trim() : null;
        this.precioCop       = precioCop;
        this.precioUsd       = precioUsd;
        this.precioEur       = precioEur;
        this.empresaNit      = empresaNit;
        this.activo          = activo;
        this.categorias      = (categorias != null) ? List.copyOf(categorias) : List.of();
        this.creadoEn        = creadoEn;
        this.actualizadoEn   = actualizadoEn;
    }

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

    /**
     * @return código del producto como Value Object; nunca {@code null}.
     */
    public CodigoProducto getCodigo() {
        return codigo;
    }

    /**
     * Conveniencia: devuelve el valor String del código sin navegar al VO.
     *
     * @return representación textual del código (ej.: "PROD-001").
     */
    public String getCodigoValor() {
        return codigo.getValor();
    }

    public String getNombre() {
        return nombre;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    /**
     * @return precio en pesos colombianos (COP) como Value Object {@link Dinero}.
     */
    public Dinero getPrecioCop() {
        return precioCop;
    }

    /**
     * @return precio en dólares estadounidenses (USD) como Value Object {@link Dinero}.
     */
    public Dinero getPrecioUsd() {
        return precioUsd;
    }

    /**
     * @return precio en euros (EUR) como Value Object {@link Dinero}.
     */
    public Dinero getPrecioEur() {
        return precioEur;
    }

    /**
     * @return NIT de la empresa propietaria del producto como Value Object.
     */
    public NitEmpresa getEmpresaNit() {
        return empresaNit;
    }

    /**
     * Conveniencia: devuelve el NIT como String sin navegar al VO.
     *
     * @return representación textual del NIT de la empresa propietaria.
     */
    public String getEmpresaNitValor() {
        return empresaNit.getValor();
    }

    public boolean isActivo() {
        return activo;
    }

    /**
     * Devuelve una vista inmutable de las categorías asociadas al producto.
     * Nunca devuelve {@code null}; si no hay categorías, retorna una lista vacía.
     *
     * @return lista inmutable de categorías.
     */
    public List<Categoria> getCategorias() {
        return categorias;
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
     * Devuelve el precio del producto en la moneda solicitada.
     *
     * @param moneda moneda en la que se desea consultar el precio; nunca {@code null}.
     * @return instancia de {@link Dinero} con el precio en la moneda indicada.
     * @throws NullPointerException     si {@code moneda} es {@code null}.
     * @throws IllegalArgumentException si la moneda solicitada no está soportada.
     */
    public Dinero getPrecioEn(Moneda moneda) {
        Objects.requireNonNull(moneda, "La moneda no puede ser nula.");
        return switch (moneda) {
            case COP -> precioCop;
            case USD -> precioUsd;
            case EUR -> precioEur;
        };
    }

    /**
     * Indica si el producto pertenece a una categoría específica.
     *
     * @param categoria categoría a verificar; nunca {@code null}.
     * @return {@code true} si el producto está asociado a esa categoría.
     */
    public boolean perteneceACategoria(Categoria categoria) {
        Objects.requireNonNull(categoria, "La categoría a verificar no puede ser nula.");
        return categorias.contains(categoria);
    }

    /**
     * Indica si el producto puede incluirse en nuevas órdenes de compra.
     *
     * @return {@code true} si el producto está activo.
     */
    public boolean estaDisponible() {
        return this.activo;
    }

    // -------------------------------------------------------------------------
    // Validación interna
    // -------------------------------------------------------------------------

    private void validarMonedaDeprecio(Dinero precio, Moneda monedaEsperada, String etiqueta) {
        if (!monedaEsperada.equals(precio.getMoneda())) {
            throw new IllegalArgumentException(
                    "El precio " + etiqueta + " debe tener moneda " + monedaEsperada.getCodigoIso() +
                    " pero se recibió: " + precio.getMoneda().getCodigoIso() + "."
            );
        }
    }

    // -------------------------------------------------------------------------
    // Igualdad por código (identidad de negocio), hashCode y representación
    // -------------------------------------------------------------------------

    /** La identidad de negocio de un {@code Producto} está determinada por su código. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto other)) return false;
        return codigo.equals(other.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Producto{codigo=" + codigo + ", nombre='" + nombre +
               "', empresa=" + empresaNit + ", activo=" + activo + "}";
    }
}
