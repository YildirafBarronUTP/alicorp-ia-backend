package com.alicorp.logistica.repository;

import com.alicorp.logistica.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    // Método para obtener los últimos despachos ordenados por fecha
    List<Despacho> findAllByOrderByFechaRegistroDesc();
}