package com.example.Recu_RA3.Repository;

import com.example.Recu_RA3.Entity.Camiones;
import com.example.Recu_RA3.Entity.EstadoCamion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamionesRepository extends JpaRepository<Camiones, Integer> {
    List<Camiones> findAllByUsuario_Id(int usuarioId);
    List<Camiones> findByEstado(EstadoCamion estado);
    List<Camiones> findByActivoTrue();
}