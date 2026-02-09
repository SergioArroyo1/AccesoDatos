package com.example.Recu_RA3.Repository;

import com.example.Recu_RA3.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Usuario> findUsuarioByUsername(String username);
    Optional<Usuario> findUsuarioById(int id);

    // Método para Spring Security
    Optional<Usuario> findByUsername(String username);
}