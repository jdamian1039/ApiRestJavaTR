package com.jorged.almacen.dto.sucursales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SucursalRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 5, max =50, message = "El nombre debe tener de 5-50 caracteres")
        String nombre,
        @NotBlank(message = "La direccion es requerida")
        @Size(min = 10, max =150, message = "La direccion debe tener de 10-150 caracteres")
        String direccion
) {


}
