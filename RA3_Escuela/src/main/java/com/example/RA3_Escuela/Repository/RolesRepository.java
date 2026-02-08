package com.example.RA3_Escuela.Repository;

import com.example.RA3_Escuela.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByNombreRol(String nombreRol);
}
