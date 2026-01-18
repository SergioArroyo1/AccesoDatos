package com.example.RA3.service;

import com.example.RA3.model.Usuario;
import com.example.RA3.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Usuario CrearUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario viene al service.");

        if (usuarioRepository.existsByUsername(usuario.getUsername()))
            throw new IllegalArgumentException("El usuario ya existe");

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        return usuarioRepository.save(usuario);

    }


    public boolean comprobarUsername(String username) {

        if (!StringUtils.hasText(username)) throw new IllegalArgumentException("El username no puede estar vacio.");

        return usuarioRepository.existsByUsername(username);

    }


    public boolean comprobarInicioSesion(Usuario usuarioraw) {

        if (usuarioraw == null) throw new IllegalArgumentException("El usuario viene vacio al service.");

        // El optional sirve, para si no existe un usuario lo mete en una caja con un valor nulo y o rompe el programa.
        Optional<Usuario> usuarioBD = usuarioRepository.searchUsuarioByUsername(usuarioraw.getUsername());

        // Comprueba si esta vacio el usuario
        if (usuarioBD.isEmpty()) throw new IllegalStateException("El usuario no existe. ");

        //Verifica que la contraseña que mete el usuario compara una contraseña sin hasear con una haseada
        if (!passwordEncoder.matches(usuarioraw.getContrasena(), usuarioBD.get().getContrasena()))
            throw new IllegalStateException("La contraseña no coincide");

        return true;
    }

    public List<Usuario> listaUsuarios() {
        return usuarioRepository.findAllBy();

    }

    public Optional<Usuario> buscaUsuario(String username) {
        if (username == null) throw new IllegalArgumentException("El nombre introducido no existe");
        Optional<Usuario> usuarioBuscado = usuarioRepository.findUsuarioByUsername(username);
        if (usuarioBuscado.isEmpty()) throw new IllegalStateException("El usuario es nulo");
        return usuarioBuscado;
    }

    public Usuario actualizaUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El username no existe");
        Optional<Usuario> usuarioParaActualizar = usuarioRepository.findUsuarioById(usuario.getId());

        if (usuarioParaActualizar.isEmpty()) throw new IllegalStateException("El usuario no existe");

        if (usuario.getContrasena().equals(usuarioParaActualizar.get().getContrasena())) {
            return usuarioRepository.save(usuario);

        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);

    }

    public Usuario desactivoUsuario(String username){
            Optional <Usuario> usuario = Optional.empty();

            if(StringUtils.hasText(username)){
                if(!comprobarUsername(username)){
                    throw new IllegalStateException("El username no existe");
                }
                usuario = buscaUsuario(username);
                if(usuario.get().isActivo()){
                    usuario.get().setActivo(false);
                } else {
                    usuario.get().setActivo(true);

                }
            }
            return  usuarioRepository.save(usuario.get());
        }
    @Transactional
    public boolean eliminaUsuario(String username){
        return usuarioRepository.deleteByUsername(username) > 0 ;
    }
}


