package com.jorged.almacen.mapper;

import com.jorged.almacen.dto.ventas.VentaRequest;
import com.jorged.almacen.entities.Venta;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {
    public Venta requestAEntidad(VentaRequest venta){
        if (venta==null) return null;
        return Venta.builder().build();
    }
}
