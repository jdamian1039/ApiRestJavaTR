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
@Table(name = "Productos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCTO")
    private Long id;
    @Column(name = "NOMBRE", length = 30, nullable = false)
    private String nombre;
    @Column(name = "CATEGORIA", nullable = false)
    @Enumerated(EnumType.STRING) // guarda el enum en cadena de caracter en lugar del indice
    private Categoria categoria;
    @Column(name = "PRECIO", nullable = false)
    private BigDecimal precio;
    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    public Producto(String nombre, Categoria categoria, BigDecimal precio, Integer cantidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public void aumentarCantidad(int cantidad){
        if (cantidad < 0)
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        this.cantidad += cantidad;
    }

    public void descontarCantidad(int cantidad){
        if (cantidad < 0 || cantidad > this.cantidad)
            throw new IllegalArgumentException("La cantidad debe ser positiva y mayor o igual a la cantidad actual");
        this.cantidad -= cantidad;
    }

    public void validarDatosProducto(String nombre, Categoria categoria, BigDecimal precio, Integer cantidad) {
        StringCustomUtils.validarTamanio(nombre, 5, 30,
                "El nombre es requerido y debe tener entre 5 y 30 caracteres");

        if (categoria == null) throw new IllegalArgumentException("La categoria es requerida");

        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El precio es requerido y debe ser positivo");

        if (cantidad == null || cantidad < 0)
            throw new IllegalArgumentException("La cantidad es requerida y debe ser positivo");
    }

    public void actualiza(String nombre, Categoria categoria, BigDecimal precio, Integer cantidad) {
       validarDatosProducto(nombre, categoria, precio, cantidad);
       this.nombre = nombre;
       this.categoria = categoria;
       this.cantidad = cantidad;
       this.precio = precio;
    }
}
