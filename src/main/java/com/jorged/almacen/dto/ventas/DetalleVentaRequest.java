package com.jorged.almacen.dto.ventas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DetalleVentaRequest(
        @NotNull(message = "Id de producto requerido")
        @Positive(message = "Id de producto debe ser positivo")
        Long idProducto,
        @NotNull(message = "Cantidad de producto requerida")
        @Positive(message = "Cantidad de producto debe ser positivo")
        Integer cantidadProducto,
        @NotNull(message = "Precio de producto requerida")
        @Positive(message = "Precio de producto debe ser positivo")
        BigDecimal precioProducto
) {
}
