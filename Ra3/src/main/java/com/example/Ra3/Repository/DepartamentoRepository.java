package com.example.Ra3.Repository;

import com.example.Ra3.Modelo.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Repositorio JPA para la entidad Departamento.
 * Proporciona CRUD y búsquedas por nombre.
 */
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    /** ¿Existe un departamento con este nombre? */
    boolean existsByNombre(String nombre);
    /** Busca un departamento por su nombre */
    Optional<Departamento> findByNombre(String nombre);
}
