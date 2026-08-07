package com.jorged.almacen.services.ventas;

import com.jorged.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.jorged.almacen.dto.ventas.VentaRequest;
import com.jorged.almacen.dto.ventas.VentaResponse;
import com.jorged.almacen.entities.Venta;
import org.springframework.stereotype.Service;

import java.util.List;

public interface VentasService {
    List<VentaResponse> listarVentasActivas();
    List<VentaResponse> listarVentasCanceladas();
    VentaResponse listarActivaPorId(Long id);
    VentaResponse registrarVenta(VentaRequest request);
    void cancelarVenta(Long id);
    List<ReporteVentasSucursalResponse> generarReporte();
}
