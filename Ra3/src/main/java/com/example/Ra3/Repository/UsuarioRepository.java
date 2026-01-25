package com.example.Ra3.Repository;

import com.example.Ra3.Modelo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repositorio JPA para usuarios.
 * Incluye utilidades adicionales usadas por los servicios/controladores.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /** Comprueba si existe un username en la BD */
    boolean existsByUsername(String username);
    /** Obtiene un usuario por username (para login/validaciones) */
    Optional<Usuario> findUsuarioByUsername(String username);
    /** Obtiene un usuario por id */
    Optional<Usuario> findUsuarioById(Long id);

    // Para compatibilidad con el servicio actual
    /** Variación del find por username (nomenclatura antigua usada en el service) */
    Optional<Usuario> findAllByUsername(String username);
    /** Listado completo de usuarios (sin paginación) */
    ArrayList<Usuario> findAllBy();

    /** Elimina usuarios por username y devuelve cuántos registros afectó */
    int deleteByUsername(String username);

    // Ejercicio 3: paginación (ya disponible por JpaRepository)
    /** Paginación de usuarios */
    Page<Usuario> findAll(Pageable pageable);
    /** Comprueba si existe un email en la BD */
    boolean existsByEmail(String email);

    // NUEVO: cuantos usuarios referencian un departamento (para borrado seguro)
    /** Cuenta cuántos usuarios tienen asignado el departamento con ese ID */
    long countByDepartamento_Id(Long departamentoId);
}