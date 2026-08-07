package com.jorged.almacen.mapper;

import com.jorged.almacen.dto.sucursales.SucursalResponse;
import com.jorged.almacen.dto.ventas.*;
import com.jorged.almacen.entities.DetalleVenta;
import com.jorged.almacen.entities.Producto;
import com.jorged.almacen.entities.Sucursal;
import com.jorged.almacen.entities.Venta;
import com.jorged.almacen.enums.EstadoVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class VentaMapper {
    public Venta requestAEntidadVenta(VentaRequest request, EstadoVenta estadoVenta, Sucursal sucursal){
        if (request==null||sucursal==null)return null;

        return Venta.builder()
                .estadoVenta(estadoVenta)
                .fecha(LocalDate.now())
                .sucursal(sucursal)
                .build();
    }

    public VentaResponse entidadAResponseVenta(Venta venta, List<DetalleVentaResponse> detalles, SucursalResponse sucursalResponse, BigDecimal total){
        if (venta==null||detalles==null||sucursalResponse==null) return null;

        return new VentaResponse(
                venta.getId(),
                venta.getFecha().toString(),
                venta.getEstadoVenta().getDescripcion(),
                sucursalResponse,
                detalles, total);
    }
    public DetalleVenta requestAEntidadDetalle(DetalleVentaRequest request, Producto producto, Venta venta){
        if (request==null || producto==null || venta ==null) return null;

        return DetalleVenta.builder()
                .cantidadProducto(request.cantidadProducto())
                .precioProducto(producto.getPrecio())
                .producto(producto)
                .venta(venta)
                .build();
    }

    public DetalleVentaResponse entidadAResponseDetalle(DetalleVenta detalle){
        if (detalle==null) return null;

        return new DetalleVentaResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidadProducto(),
                detalle.getPrecioProducto(),
                detalle.obtenerSubtotal()
        );
    }

    public ReporteVentasSucursalResponse generarResponse(Long idSucursal, String nombreSucursal, BigDecimal facturado, Integer cantidad){
        return new ReporteVentasSucursalResponse(
                idSucursal,
                nombreSucursal,
                facturado,
                cantidad
        );
    }
}
