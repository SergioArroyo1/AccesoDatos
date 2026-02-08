package com.example.RA3_final.Service;
import com.example.RA3_final.DTO.LoginDTO;
import com.example.RA3_final.Entity.Usuario;
import com.example.RA3_final.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    public boolean comprobarInicioSesion(LoginDTO logindto){
        if (logindto == null) throw new IllegalArgumentException("El usuario viene vacio al service");
        Optional<Usuario> usuariobd = usuarioRepository.findUsuarioByUsername(logindto.getUsername());
        if(usuariobd.isEmpty()) throw new IllegalStateException("El usuario no existe");

        if(!passwordEncoder.matches(logindto.getPassword(),usuariobd.get().getPasswordHash())) throw new IllegalStateException("Las contraseñas no coinciden");
        return true;
    }

    public Optional<Usuario> obtenerUsuarioPorUsername(String username){
        return usuarioRepository.findUsuarioByUsername(username);
    }

    public Usuario crearUsuario(Usuario usuario){

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El username ya existe: " + usuario.getUsername());
        }
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya existe: " + usuario.getEmail());
        }
        // Hashear la contraseña con BCrypt
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        return usuarioRepository.save(usuario);
    }

    //Actualizar usuario
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuario nulo");

        Optional<Usuario> existe = usuarioRepository.findUsuarioById(usuario.getId());
        if (existe.isEmpty()) throw new IllegalStateException("El usuario no existe");
        return usuarioRepository.save(usuario);
    }


//Eliminar usuario
    @Transactional
    public void eliminarUsuario(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> mostrarusuarios(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(int id) {
        return usuarioRepository.findById(id);
    }
}
