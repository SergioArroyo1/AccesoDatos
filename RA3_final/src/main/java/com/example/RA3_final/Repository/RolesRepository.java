package com.example.RA3_final.Repository;

import com.example.RA3_final.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByNombreRol (String nombre);
}
