package com.example.RA3_Escuela.Service;

import com.example.RA3_Escuela.DTO.LoginDTO;
import com.example.RA3_Escuela.Entity.Usuario;
import com.example.RA3_Escuela.Repository.UsuarioRepository;
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

    public boolean comprobarIniciarSesion(LoginDTO loginDTO){
        if(loginDTO == null) throw new IllegalArgumentException("El usuario viene vacio");
        Optional<Usuario> usuariobd = usuarioRepository.findUsuarioByUsername(loginDTO.getUsername());
        if(usuariobd.isEmpty()) throw new IllegalStateException("El usuario no existe");

        if(!passwordEncoder.matches(loginDTO.getPassword(),usuariobd.get().getPasswordHash())) throw new IllegalStateException("Las contraseñas no coinciden");
        return true;
    }

    public Optional<Usuario> obtenerUsuarioPorUsername(String username){
        return usuarioRepository.findUsuarioByUsername(username);
    }

    public Usuario crearUsuario(Usuario usuario){
        // Validar que no exista el username
        if(usuarioRepository.existsByUsername((usuario.getUsername()))) {
            throw  new IllegalArgumentException("El username ya existe: " + usuario.getUsername());

        }
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new IllegalArgumentException("El email ya existe: " + usuario.getEmail());
        }
        // Hashear la contraseña con BCrypt
        usuario.setPasswordHash((passwordEncoder.encode(usuario.getPasswordHash())));
        return usuarioRepository.save(usuario);

    }

    @Transactional
    public Usuario actualizarUsuario(Usuario usuario){
        if(usuario == null) throw new IllegalArgumentException("El usuario es nulo");

        Optional<Usuario> existeUSU = usuarioRepository.findUsuarioById(usuario.getId());
        if(existeUSU.isEmpty()) throw new IllegalStateException("El usuario no existe");

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(int id){
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> mostrarUsuarios(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(int id){
        return usuarioRepository.findUsuarioById(id);
    }

}
