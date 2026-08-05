package com.jorged.almacen.entities;

import com.jorged.almacen.enums.Categoria;
import com.jorged.almacen.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="Sucursales")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SUCURSAL")
    private Long id;
    @Column(name="NOMBRE", unique=true, length = 50, nullable = false)
    private String nombre;
    @Column(name="DIRECCION", length = 150, nullable = false)
    private String direccion;

    public Sucursal(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public void actualizar(String nombre, String direccion) {
        validarDatos(nombre, direccion);
        this.nombre = nombre.trim();
        this.direccion = direccion.trim();
    }

    public void validarDatos(String nombre, String direccion) {
        StringCustomUtils.validarTamanio(nombre, 5, 30,
                "El nombre es requerido y debe tener entre 5 y 500 caracteres");

        StringCustomUtils.validarTamanio(direccion, 10, 150,
                "El nombre es requerido y debe tener entre 10 y 150 caracteres");

    }
}
