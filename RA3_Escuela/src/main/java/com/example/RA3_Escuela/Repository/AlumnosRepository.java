package com.example.RA3_Escuela.Repository;

import com.example.RA3_Escuela.Entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlumnosRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findAllByUsuario_Id(int id);
}
