package com.jorged.almacen.controller;

import com.jorged.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.jorged.almacen.dto.ventas.VentaRequest;
import com.jorged.almacen.dto.ventas.VentaResponse;
import com.jorged.almacen.services.ventas.VentasService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@AllArgsConstructor
@Validated
public class VentaController {

    private final VentasService ventasService;

    @GetMapping("/activas")
    public ResponseEntity<List<VentaResponse>> listarVentasActivas() {
        return ResponseEntity.ok(ventasService.listarVentasActivas());
    }

    @GetMapping("/canceladas")
    public ResponseEntity<List<VentaResponse>> listarVentasCanceladas() {
        return ResponseEntity.ok(ventasService.listarVentasCanceladas());
    }
    @GetMapping("/activas/{id}")
    public ResponseEntity<VentaResponse> obtenerVentaActiva(@PathVariable @Positive(message = "El id debe ser positivo") Long id) {
        return ResponseEntity.ok(ventasService.listarActivaPorId(id));
    }
    @GetMapping("/genera-reporte")
    public ResponseEntity<List<ReporteVentasSucursalResponse>> generarReporte() {
        return ResponseEntity.ok(ventasService.generarReporte());
    }
    @PostMapping
    public ResponseEntity<VentaResponse> registrarVenta(@Valid @RequestBody VentaRequest request){
        return ResponseEntity.ok(ventasService.registrarVenta(request));
    }
    @DeleteMapping("/cancelar/{id}")
    public void cancelarVenta(@PathVariable @Positive(message = "El id debe ser positivo") Long id){
        ventasService.cancelarVenta(id);
    }
}
