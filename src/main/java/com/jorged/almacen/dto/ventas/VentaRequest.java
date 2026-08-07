package com.jorged.almacen.dto.ventas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record VentaRequest(
        @NotNull(message = "Id de producto requerido")
        @Positive(message = "Id de producto debe ser positivo")
        Long idSucursal,
        @NotEmpty(message = "Lista de productos requerida. No debe estar vacía")
        List<@Valid DetalleVentaRequest> productos
) {
}
