package com.litethinking.backend.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para crear o actualizar un {@code Producto}.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que transporta los datos
 * validados desde el adaptador de entrada (controlador REST) hacia los casos de uso
 * {@code CrearProductoUseCase} y {@code ActualizarProductoUseCase}.</p>
 *
 * <p><strong>Validaciones aplicadas:</strong></p>
 * <ul>
 *   <li>{@code codigo}: obligatorio, máximo 50 caracteres.</li>
 *   <li>{@code nombre}: obligatorio, máximo 200 caracteres.</li>
 *   <li>{@code precioCop}, {@code precioUsd}, {@code precioEur}: obligatorios, no negativos.</li>
 *   <li>{@code empresaNit}: obligatorio (referencia a la empresa propietaria).</li>
 *   <li>{@code categoriaIds}: opcional; si se provee, deben ser IDs válidos existentes.</li>
 * </ul>
 *
 * <p>El mapeo a Value Objects del dominio ({@code CodigoProducto}, {@code Dinero}, {@code NitEmpresa})
 * se realiza en la implementación del caso de uso.</p>
 */
public record ProductoRequest(

        @NotBlank(message = "El código del producto es obligatorio")
        @Size(max = 50, message = "El código no puede superar los 50 caracteres")
        String codigo,

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
        String nombre,

        @Size(max = 1000, message = "Las características no pueden superar los 1000 caracteres")
        String caracteristicas,

        @NotNull(message = "El precio en COP es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio en COP no puede ser negativo")
        BigDecimal precioCop,

        @NotNull(message = "El precio en USD es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio en USD no puede ser negativo")
        BigDecimal precioUsd,

        @NotNull(message = "El precio en EUR es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio en EUR no puede ser negativo")
        BigDecimal precioEur,

        @NotBlank(message = "El NIT de la empresa propietaria es obligatorio")
        String empresaNit,

        /**
         * IDs de las categorías a asociar al producto.
         * Puede ser {@code null} o vacío para crear un producto sin categorías.
         */
        List<Long> categoriaIds,

        /**
         * Cantidad inicial en inventario al crear el producto.
         * Si es null, se asume 0.
         */
        Integer stockInicial

) {}
