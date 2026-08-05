package com.jorged.almacen.enums;

import com.jorged.almacen.exceptions.RecursoNoEncontradoException;
import com.jorged.almacen.utils.StringCustomUtils;
import lombok.*;

@RequiredArgsConstructor
@Getter
public enum Categoria {
    ALIMENTO("Alimento"),
    HIGIENE("Higiene"),
    JUGUETE("Juguete"),
    ELECTRONICA("Electrónica"),
    ROPA("Ropa"),
    ACCESORIO("Accesorio"),
    FARMACIA("Farmacia");

    private final String descripcion;

    public static Categoria obtenerCategoriaPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNorm = StringCustomUtils.quitarAcentos(descripcion);
        for (Categoria categoria: values()){
            if (StringCustomUtils.quitarAcentos(categoria.descripcion).equalsIgnoreCase(descripcionNorm))
                return categoria;
        }
        throw new RecursoNoEncontradoException("No existe categoria con la descripción:" + descripcion);
    }
}
