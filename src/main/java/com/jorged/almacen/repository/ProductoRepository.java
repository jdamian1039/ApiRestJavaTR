package com.jorged.almacen.repository;

import com.jorged.almacen.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {


    @Query(value = "SELECT * FROM PRODUCTOS " +
            "WHERE (:nombre IS NULL OR UPPER(nombre) LIKE UPPER(:nombre)) " +
            "AND (:categoria IS NULL OR UPPER(categoria) = UPPER(:categoria)) " +
            "AND (:precioMin IS NULL OR precio >= :precioMin) " +
            "AND (:precioMax IS NULL OR precio <= :precioMax)",
            nativeQuery = true)


    List<Producto> busquedaPorParametros(
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax);
}
