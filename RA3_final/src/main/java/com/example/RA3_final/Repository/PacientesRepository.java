package com.example.RA3_final.Repository;

import com.example.RA3_final.Entity.Pacientes;
import com.example.RA3_final.Entity.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacientesRepository extends JpaRepository<Pacientes, Integer> {
    List<Pacientes> findAllByUsuario_Id(int usuarioId);


}

