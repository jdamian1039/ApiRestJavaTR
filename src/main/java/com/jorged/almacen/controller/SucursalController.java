package com.jorged.almacen.controller;

import com.jorged.almacen.dto.sucursales.SucursalRequest;
import com.jorged.almacen.dto.sucursales.SucursalResponse;
import com.jorged.almacen.services.sucursales.SucursalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@AllArgsConstructor
@Validated
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listar(){
        return ResponseEntity.ok(sucursalService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> obtenerPorId(@PathVariable @Positive(message = "El id debe ser positivo") Long id){
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }
    @PostMapping
    public ResponseEntity<SucursalResponse> registrar(@Valid @RequestBody SucursalRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.registrar(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> actualizar(
            @Valid @RequestBody SucursalRequest request,
            @PathVariable @Positive(message = "Id debe ser positivo")Long id){
        return ResponseEntity.ok(sucursalService.actualizar(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive(message = "Id debe ser positivo")Long id){
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
