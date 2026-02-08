package com.example.RA3_final.Repository;

import com.example.RA3_final.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Usuario> findUsuarioByUsername(String username);

    Optional<Usuario> findUsuarioById(int id);
}
