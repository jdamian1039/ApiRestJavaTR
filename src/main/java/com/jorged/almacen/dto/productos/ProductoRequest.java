package com.jorged.almacen.dto.productos;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record ProductoRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 5, max =30, message = "El nombre debe tener de 5-30 caracteres")
        String nombre,
        @NotBlank( message = "Categoria es requerida")
        String categoria,
        @NotNull(message = "precio es requerida")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal precio,
        @NotNull(message = "cantidad es requerida")
        @Positive(message = "La cantidad debe ser positiva")
        Integer cantidad) {

}
