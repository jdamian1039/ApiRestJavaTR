package com.jorged.almacen.services.sucursales;

import com.jorged.almacen.dto.sucursales.SucursalRequest;
import com.jorged.almacen.dto.sucursales.SucursalResponse;
import com.jorged.almacen.entities.Sucursal;
import com.jorged.almacen.exceptions.RecursoNoEncontradoException;
import com.jorged.almacen.mapper.SucursalMapper;
import com.jorged.almacen.repository.SucursalRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class SucursalServiceImp implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    @Override
    public List<SucursalResponse> listar() {
        return sucursalRepository.findAll().stream().map(sucursalMapper::entidadAResponse).toList();
    }

    @Override
    public SucursalResponse obtenerPorId(Long id) {
        return sucursalMapper.entidadAResponse(obtenerSucursalOException(id));
    }

    @Override
    public SucursalResponse registrar(SucursalRequest request) {
        validarDatosUnicos(request);
        Sucursal sucursal = sucursalMapper.requestAEntidad(request);
        sucursalRepository.save(sucursal);
        return sucursalMapper.entidadAResponse(sucursal);
    }

    @Override
    public SucursalResponse actualizar(SucursalRequest request, Long id) {
        Sucursal sucursal = obtenerSucursalOException(id);

        validarCambiosUnicos(request, id);
        sucursal.actualizar(request.nombre(), request.direccion());

        return sucursalMapper.entidadAResponse(sucursal);
    }

    @Override
    public void eliminar(Long id) {
        Sucursal sucursal = obtenerSucursalOException(id);

        sucursalRepository.delete(sucursal);
    }

    private Sucursal obtenerSucursalOException(Long id){
        log.info("Buscar sucursal con id: {}", id);

        return sucursalRepository.findById(id).
                orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada con id: " + id));
    }

    private void validarDatosUnicos(SucursalRequest request){
        if (sucursalRepository.existsByNombreIgnoreCase(request.nombre().trim()))
            throw new IllegalArgumentException("Ya existe una sucursal con nombre " + request.nombre());

    }
    private void validarCambiosUnicos(SucursalRequest request, Long id){
        if (sucursalRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id))
            throw new IllegalArgumentException("Ya existe una sucursal con nombre " + request.nombre());

    }
}
