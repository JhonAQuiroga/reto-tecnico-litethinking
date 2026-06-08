package com.litethinking.backend.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para crear una Orden.
 */
@Getter
@NoArgsConstructor
public class OrdenRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "La orden debe tener al menos un producto")
    @Valid
    private List<OrdenProductoRequest> productos;

    @Getter
    @NoArgsConstructor
    public static class OrdenProductoRequest {

        @NotBlank(message = "El código del producto es obligatorio")
        private String productoCodigo;

        @Min(value = 1, message = "La cantidad mínima es 1")
        private int cantidad;

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero")
        private BigDecimal precioUnitario;
    }
}
