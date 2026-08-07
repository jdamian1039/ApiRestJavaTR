package com.jorged.almacen.repository;

import com.jorged.almacen.dto.ventas.VentaResponse;
import com.jorged.almacen.entities.DetalleVenta;
import com.jorged.almacen.entities.Venta;
import com.jorged.almacen.enums.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> getVentasByEstadoVenta(EstadoVenta estadoVenta);

    List<Venta> getVentasBySucursal_Id(Long sucursalId);
}
