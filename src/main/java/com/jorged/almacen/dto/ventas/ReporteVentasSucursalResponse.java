package com.jorged.almacen.dto.ventas;

import java.math.BigDecimal;

public record ReporteVentasSucursalResponse(
        Long idSucursal,
        String nombreSucursal,
        BigDecimal totalFacturado,
        Integer productosVendidos
) {
}
