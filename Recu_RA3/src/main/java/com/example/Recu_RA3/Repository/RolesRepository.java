package com.example.Recu_RA3.Repository;

import com.example.Recu_RA3.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByNombreRol (String nombre);
}
