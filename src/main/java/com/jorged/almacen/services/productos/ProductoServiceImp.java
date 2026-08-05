package com.jorged.almacen.services.productos;

import com.jorged.almacen.dto.productos.ProductoRequest;
import com.jorged.almacen.dto.productos.ProductoResponse;
import com.jorged.almacen.entities.Producto;
import com.jorged.almacen.enums.Categoria;
import com.jorged.almacen.exceptions.RecursoNoEncontradoException;
import com.jorged.almacen.mapper.ProductoMapper;
import com.jorged.almacen.repository.ProductoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional //envuelve a todos los metodos PUBLICOS Y con @Overwrite || si algo falla hace rollback como grupo de sentencias en bd
@Slf4j
public class ProductoServiceImp implements ProductoService{

    private final ProductoRepository productoRepository;

    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String nombre, String categoria, BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findAll().stream().map(productoMapper::entidadAResponse).toList();
        // return .map(producto - productoMapper.entidadAResponse(producto))
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.entidadAResponse(obtenerProductoOException(id));
    }

    @Override
    public ProductoResponse registrar(ProductoRequest request) {
        log.info("Registra nuevo producto");
        Categoria categoria = obtenerCategoriaPorDescripcion(request.categoria());

        Producto producto = productoMapper.requestAEntidad(request, categoria);
        productoRepository.save(producto);
        log.info("Nuevo producto {} registrado", producto.getNombre());

        return productoMapper.entidadAResponse(producto);
    }

    @Override
    public ProductoResponse actualizar(ProductoRequest request, Long id) {

        Producto producto = obtenerProductoOException(id);
        Categoria categoria = obtenerCategoriaPorDescripcion(request.categoria());
        log.info("Actualizando producto con id {}", id);

        producto.actualiza(request.nombre(), categoria, request.precio(), request.cantidad());

        log.info("Producto id {} actualizado", id);

        return productoMapper.entidadAResponse(producto);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = obtenerProductoOException(id);
        log.info("Eliminando producto con id {}", id);

        productoRepository.delete(producto);
    }

    private Producto obtenerProductoOException(Long id){
        log.info("Buscar producto con id: {}", id);

        return productoRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    private Categoria obtenerCategoriaPorDescripcion(String desc){
        return Categoria.obtenerCategoriaPorDescripcion(desc.trim());
    }
}
