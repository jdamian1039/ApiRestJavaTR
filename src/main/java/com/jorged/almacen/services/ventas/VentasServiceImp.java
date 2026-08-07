package com.jorged.almacen.services.ventas;

import com.jorged.almacen.dto.ventas.*;
import com.jorged.almacen.entities.DetalleVenta;
import com.jorged.almacen.entities.Producto;
import com.jorged.almacen.entities.Sucursal;
import com.jorged.almacen.entities.Venta;
import com.jorged.almacen.enums.EstadoVenta;
import com.jorged.almacen.exceptions.RecursoNoEncontradoException;
import com.jorged.almacen.mapper.VentaMapper;
import com.jorged.almacen.repository.ProductoRepository;
import com.jorged.almacen.repository.SucursalRepository;
import com.jorged.almacen.repository.VentaRepository;
import org.springframework.transaction.annotation.Transactional;

/*
* Hola huapo como estas?,
* Te mando besitos chiquto,xoxoxo
* Cuidate bb xd
* action(beso)
* */
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class VentasServiceImp implements VentasService{
    private final VentaRepository ventasRepository;
    private final VentaMapper ventaMapper;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public VentaResponse listarActivaPorId(Long id) {

        Venta venta = ventasRepository.findById(id).filter(Venta::validarVentaRegistrada).orElseThrow(() -> new RecursoNoEncontradoException("Sin registro de venta " + id));
        log.info(venta.getDetalleVentas().toString());

        return ventaMapper.entidadAResponseVenta(venta, venta.getSucursal(), venta.obtenerTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentasActivas() {

        return ventasRepository.getVentasByEstadoVenta(EstadoVenta.REGISTRADA).stream()
                .map(venta -> ventaMapper.entidadAResponseVenta(venta,
                        venta.getSucursal(), venta.obtenerTotal()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentasCanceladas() {

        return ventasRepository.getVentasByEstadoVenta(EstadoVenta.CANCELADA).stream()
                .map(venta -> ventaMapper.entidadAResponseVenta(venta,
                        venta.getSucursal(), venta.obtenerTotal()))
                .toList();
    }

    @Override
    public VentaResponse registrarVenta(VentaRequest request) {
        log.info("Obtener la sucursal de la venta...");
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal()).
                orElseThrow(()->new RecursoNoEncontradoException("Sin sucursal con id " + request.idSucursal()));
        log.info("sucursal obtenida");
        log.info("Obteneniendo datos de la Venta...");
        Venta venta = ventaMapper.requestAEntidadVenta(request, EstadoVenta.REGISTRADA, sucursal);
        log.info("Validando stock de la venta...");
        request.productos().forEach(this::validarCantidadesdeCompra);

        List<DetalleVenta> lista = request.productos().stream()
                .map(detalleVentaRequest ->
                        ventaMapper.requestAEntidadDetalle(detalleVentaRequest, obtenerProducto(detalleVentaRequest.idProducto()), venta))
                .toList();
        log.info("Ingresando la venta al sistema...");
        lista.forEach(venta::agregarDetalle);
        ventasRepository.save(venta);
        log.info("Generando respuesta...");
        return ventaMapper.entidadAResponseVenta(venta, venta.getSucursal(), venta.obtenerTotal());
    }

    @Override
    public void cancelarVenta(Long id) {
        Venta venta = ventasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Venta "+ id + " no existe."));
        if(!venta.validarVentaCancelada()){
            log.info("Incia proceso de cancelacion...");
            venta.cancelar();
            log.info("Devolviendo al stock cancelacion...");
            venta.getDetalleVentas().forEach(this::realizarDevolucionStock);
            ventasRepository.save(venta);
        }
        throw new IllegalArgumentException("La venta " + id + " ya fue cancelada.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteVentasSucursalResponse> generarReporte() {

        return sucursalRepository.findAll().stream().map(this::generaReporteSucursal).toList();
    }

    private void realizarDevolucionStock(DetalleVenta detalle){
        Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException( "Producto no existe con id: "+ detalle.getProducto().getId()));

        producto.aumentarCantidad(detalle.getCantidadProducto());
        productoRepository.save(producto);
    }

    private void validarCantidadesdeCompra(DetalleVentaRequest request){
        log.info("{}", request.idProducto());
        Producto producto = obtenerProducto(request.idProducto());
        log.info("producto encontrado: {} {} {} {}", producto.getId(), producto.getNombre(), producto.getCategoria(), producto.getCantidad());
        if (producto.getCantidad() < request.cantidadProducto())
            throw new IllegalArgumentException("La cantidad de " + producto.getNombre() + "que solicitas es mayor a la que hay en el inventario. Valide su carrito de compra");

        producto.descontarCantidad(request.cantidadProducto());
        producto.actualiza(producto.getNombre(), producto.getCategoria(), producto.getPrecio(), producto.getCantidad());
    }

    private Producto obtenerProducto(Long id){
        return productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró ningún producto con el id" + id));
    }

    private ReporteVentasSucursalResponse generaReporteSucursal(Sucursal sucursal){
        BigDecimal total = ventasRepository.getVentasBySucursal_Id(sucursal.getId()).stream().filter(Venta::validarVentaRegistrada).map(Venta::obtenerTotal).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        Integer cantidad = ventasRepository.getVentasBySucursal_Id(sucursal.getId()).stream().filter(Venta::validarVentaRegistrada).map(Venta::obtenerCantidad).reduce(0, Integer::sum);

        return ventaMapper.generarResponse(sucursal.getId(), sucursal.getNombre(), total, cantidad);
    }
}
