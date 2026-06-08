package com.litethinking.backend.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AjustarStockRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer cantidad
) {}
