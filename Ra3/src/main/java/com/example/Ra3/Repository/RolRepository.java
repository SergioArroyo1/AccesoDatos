package com.example.Ra3.Repository;

import com.example.Ra3.Modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Rol.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {
    /** Busca un rol por su nombre (admin/user) */
    Optional<Rol> findByNombre(String nombre);
}
