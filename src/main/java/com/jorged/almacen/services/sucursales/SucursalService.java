package com.jorged.almacen.services.sucursales;

import com.jorged.almacen.dto.sucursales.SucursalRequest;
import com.jorged.almacen.dto.sucursales.SucursalResponse;

import java.util.List;

public interface SucursalService {
    List<SucursalResponse> listar();
    SucursalResponse obtenerPorId(Long id);
    SucursalResponse registrar(SucursalRequest request);
    SucursalResponse actualizar(SucursalRequest request, Long id);
    void eliminar(Long id);
}
