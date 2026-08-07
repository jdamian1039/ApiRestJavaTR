package com.jorged.almacen.mapper;

import com.jorged.almacen.dto.ventas.*;
import com.jorged.almacen.entities.DetalleVenta;
import com.jorged.almacen.entities.Producto;
import com.jorged.almacen.entities.Sucursal;
import com.jorged.almacen.entities.Venta;
import com.jorged.almacen.enums.EstadoVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class VentaMapper {
    private final SucursalMapper sucursalMapper;

    public VentaMapper(SucursalMapper sucursalMapper) {
        this.sucursalMapper = sucursalMapper;
    }

    public Venta requestAEntidadVenta(VentaRequest request, EstadoVenta estadoVenta, Sucursal sucursal){
        if (request==null||sucursal==null)return null;

        return Venta.builder()
                .estadoVenta(estadoVenta)
                .fecha(LocalDate.now())
                .sucursal(sucursal)
                .build();
    }

    public VentaResponse entidadAResponseVenta(Venta venta, Sucursal sucursal, BigDecimal total){
        if (venta==null||sucursal==null) return null;

        return new VentaResponse(
                venta.getId(),
                venta.getFecha().toString(),
                venta.getEstadoVenta().getDescripcion(),
                sucursalMapper.entidadAResponse(sucursal),
                venta.getDetalleVentas().stream().map(this::entidadAResponseDetalle).toList(), total);
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
