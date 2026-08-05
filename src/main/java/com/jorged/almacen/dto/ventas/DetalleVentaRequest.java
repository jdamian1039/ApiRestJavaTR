package com.jorged.almacen.dto.ventas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaRequest(
        @NotNull(message = "Id de producto requerido")
        @Positive(message = "Id de producto debe ser positivo")
        Long idProducto,
        @NotNull(message = "Cantidad de producto requerida")
        @Positive(message = "Cantidad de producto debe ser positivo")
        Integer cantidadProducto
) {
}
