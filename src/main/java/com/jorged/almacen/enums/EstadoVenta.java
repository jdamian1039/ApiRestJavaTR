package com.jorged.almacen.enums;

import com.jorged.almacen.exceptions.RecursoNoEncontradoException;
import com.jorged.almacen.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum EstadoVenta {
    REGISTRADA(1L,"Registrada"),
    CANCELADA(0L,"Cancelada");

    private final Long codigo;
    private final String descripcion;

    public static EstadoVenta obtenerEstadoVentaPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNorm = StringCustomUtils.quitarAcentos(descripcion);
        for (EstadoVenta estado: values()){
            if (StringCustomUtils.quitarAcentos(estado.descripcion).equalsIgnoreCase(descripcionNorm))
                return estado;
        }
        throw new RecursoNoEncontradoException("No existe categoria con la descripción:" + descripcion);
    }

    public static EstadoVenta obtenerEstadoVentaPorCodigo(Long codigo){
        if (codigo == null || codigo < 0)
            throw new IllegalArgumentException("Codigo debe ser positivo o 0");

        for (EstadoVenta estadoVenta:values()){
            if (Objects.equals(estadoVenta.codigo, codigo))
                return estadoVenta;
        }
        throw new RecursoNoEncontradoException("No existe estado de Venta con el codigo "+ codigo);
    }
}
