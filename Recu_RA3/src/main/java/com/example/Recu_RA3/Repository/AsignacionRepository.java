package com.example.Recu_RA3.Repository;

import com.example.Recu_RA3.Entity.Asignacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByCamionId(int camionId);
    List<Asignacion> findByRutaId(Long rutaId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM asignaciones WHERE camion_id = :camionId", nativeQuery = true)
    void deleteByCamionId(int camionId);}