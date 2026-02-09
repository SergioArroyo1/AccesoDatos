package com.example.Recu_RA3.Repository;

import com.example.Recu_RA3.Entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByActivaTrue();
    List<Ruta> findByZona(String zona);
}