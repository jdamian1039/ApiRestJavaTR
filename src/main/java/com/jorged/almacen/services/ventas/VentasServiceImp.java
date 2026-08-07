package com.jorged.almacen.services.ventas;

import com.jorged.almacen.dto.sucursales.SucursalResponse;
import com.jorged.almacen.dto.ventas.*;
import com.jorged.almacen.entities.DetalleVenta;
import com.jorged.almacen.entities.Producto;
import com.jorged.almacen.entities.Sucursal;
import com.jorged.almacen.entities.Venta;
import com.jorged.almacen.enums.Categoria;
import com.jorged.almacen.enums.EstadoVenta;
import com.jorged.almacen.mapper.SucursalMapper;
import com.jorged.almacen.mapper.VentaMapper;
import com.jorged.almacen.repository.ProductoRepository;
import com.jorged.almacen.repository.SucursalRepository;
import com.jorged.almacen.repository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class VentasServiceImp implements VentasService{
    private final VentaRepository ventasRepository;
    private final VentaMapper ventaMapper;
    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;
    private final ProductoRepository productoRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public VentaResponse listarActivaPorId(Long id) {
        Venta venta = ventasRepository.findById(id).filter(Venta::validarVentaRegistrada).orElseThrow(() -> new IllegalArgumentException("Sin registro de venta " + id));
        log.info(venta.getDetalleVentas().toString());
        SucursalResponse sucursal= obtenerSucursalVenta(venta.getSucursal().getId());
        return ventaMapper.entidadAResponseVenta(venta, obtenerDetalles(venta), sucursal, venta.obtenerTotal());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<VentaResponse> listarVentasActivas() {

        return ventasRepository.getVentasByEstadoVenta(EstadoVenta.REGISTRADA).stream()
                .map(venta -> ventaMapper.entidadAResponseVenta(venta, obtenerDetalles(venta),
                        obtenerSucursalVenta(venta.getSucursal().getId()), venta.obtenerTotal()))
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<VentaResponse> listarVentasCanceladas() {

        return ventasRepository.getVentasByEstadoVenta(EstadoVenta.CANCELADA).stream()
                .map(venta -> ventaMapper.entidadAResponseVenta(venta, obtenerDetalles(venta),
                        obtenerSucursalVenta(venta.getSucursal().getId()), venta.obtenerTotal()))
                .toList();
    }

    @Override
    public VentaResponse registrarVenta(VentaRequest request) {
        log.info("Obtener la sucursal de la venta...");
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal()).
                orElseThrow(()->new IllegalArgumentException("Sin sucursal con id " + request.idSucursal()));
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
        return ventaMapper.entidadAResponseVenta(venta, obtenerDetalles(venta), obtenerSucursalVenta(request.idSucursal()), venta.obtenerTotal());
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
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReporteVentasSucursalResponse> generarReporte() {

        return sucursalRepository.findAll().stream().map(this::generaReporteSucursal).toList();
    }

    private void realizarDevolucionStock(DetalleVenta detalle){
        Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new IllegalArgumentException( "Producto no existe con id: "+ detalle.getProducto().getId()));

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

    private EstadoVenta obtenerCategoriaPorDescripcion(Long codigo){
        return EstadoVenta.obtenerEstadoVentaPorCodigo(codigo);
    }

    private SucursalResponse obtenerSucursalVenta(Long id){
        return sucursalRepository.findById(id).map(sucursalMapper::entidadAResponse).orElseThrow(
                () -> new IllegalArgumentException("Sucursal no existe con el id " + id)
        );
    }

    private ReporteVentasSucursalResponse generaReporteSucursal(Sucursal sucursal){
        BigDecimal total = ventasRepository.getVentasBySucursal_Id(sucursal.getId()).stream().filter(Venta::validarVentaRegistrada).map(Venta::obtenerTotal).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        Integer cantidad = ventasRepository.getVentasBySucursal_Id(sucursal.getId()).stream().filter(Venta::validarVentaRegistrada).map(Venta::obtenerCantidad).reduce(0, Integer::sum);

        return ventaMapper.generarResponse(sucursal.getId(), sucursal.getNombre(), total, cantidad);
    }

    private List<DetalleVentaResponse> obtenerDetalles(Venta ventas){
        return ventas.getDetalleVentas().stream()
                .map(ventaMapper::entidadAResponseDetalle).toList();
    }
}
