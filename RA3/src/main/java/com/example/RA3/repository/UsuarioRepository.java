package com.example.RA3.repository;

import com.example.RA3.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Opcional, pero recomendable en exámenes
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    //Es un contenedor que representa un valor que puede estar presente o no,
    // sirviendo para manejar de forma segura la posibilidad de valores nulos y evitar errores
    Optional<Usuario> searchUsuarioByUsername(String username);

    //Utilizamos la lista y creamos el metodo find para que nos muestre mas de un usuario
    List<Usuario> findAllBy();

    Optional<Usuario> findUsuarioByUsername(String username);


    Optional<Usuario> findUsuarioById(Long id);


    long deleteByUsername(String username);
}
