package com.example.Ra3.Service;

import com.example.Ra3.Modelo.Usuario;
import com.example.Ra3.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Optional;
/**
 * Servicio de usuarios:
 * - Lógica de negocio: creación, validaciones, login y cambios de contraseña/estado.
 */
@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;//Necesario para hashear la contra

    /**
     * Crea un nuevo usuario:
     * - Valida que venga relleno
     * - Verifica unicidad de username
     * - Hashea la contraseña
     */
    public Usuario crearUsuarioo(Usuario usuario){
        if(usuario == null ) throw new IllegalStateException("El usuario viene vacio");
        if(usuarioRepository.existsByUsername(usuario.getUsername())) throw new IllegalStateException("El usuario ya existe");

        usuario.setContra(passwordEncoder.encode(usuario.getContra()));
        return usuarioRepository.save(usuario); // save inserta/actualiza
    }

    /** Comprueba si existe un username dado */
    public boolean comprobarUsername(String username){
        if(!StringUtils.hasText(username)) throw new IllegalArgumentException("El username no introducido");
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Verifica credenciales de inicio de sesión:
     * - Busca usuario por username
     * - Compara la contraseña en texto plano contra el hash guardado
     */
    public boolean comprobarInicioSesion(Usuario usuarioraw){
        if (usuarioraw == null) throw new IllegalArgumentException("El usuario viene vacio al service");
        Optional<Usuario> usuariobd = usuarioRepository.findUsuarioByUsername(usuarioraw.getUsername());
        if(usuariobd.isEmpty()) throw new IllegalStateException("El usuario no existe");

        if(!passwordEncoder.matches(usuarioraw.getContra(),usuariobd.get().getContra())) throw new IllegalStateException("Las contraseñas no coinciden");
        return true;
    }

    /** Devuelve todos los usuarios (sin paginación) */
    public ArrayList<Usuario> mostrarUsuario(){
        return usuarioRepository.findAllBy();
    }

    /** Busca un usuario por username (variación de repositorio) */
    public Optional<Usuario> buscarPorUsername(String username){
        if(StringUtils.hasText(username)){
            Optional<Usuario> usuario = usuarioRepository.findAllByUsername(username);
            return usuario;
        }else{
            throw  new IllegalArgumentException("username no introducido");
        }
    }

    /** Busca un usuario por su ID */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findUsuarioById(id);
    }

    /**
     * Actualiza un usuario (y re-hash si la contraseña cambia).
     * Se ejecuta dentro de una transacción.
     */
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario){
        if(usuario == null) throw new IllegalArgumentException("Usuario nulo");

        Optional<Usuario> existe = usuarioRepository.findUsuarioById(usuario.getId());
        if(existe.isEmpty()) throw new IllegalStateException("El usuario no existe");

        // Si cambió la contraseña, volver a hashearla
        if (!usuario.getContra().equals(existe.get().getContra())) {
            usuario.setContra(passwordEncoder.encode(usuario.getContra()));
        }
        return usuarioRepository.save(usuario);
    }

    /** Comprueba si existe el username en la BD */
    public boolean existeUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }

    /** Elimina un usuario por username (devuelve true si borró alguno) */
    @Transactional
    public boolean eliminarUsuario(String username){
        return usuarioRepository.deleteByUsername(username) > 0;
    }

    /**
     * Alterna el estado activo/inactivo de un usuario.
     * Devuelve el usuario actualizado.
     */
    public Usuario desactivarUsuario(String username){
        Optional <Usuario> usuario = Optional.empty();
        if(StringUtils.hasText(username)){
            if(!existeUsername(username)){
                throw new IllegalStateException("El username no existe");
            }
            usuario = buscarPorUsername(username);
            usuario.get().setActivo(!usuario.get().isActivo());
        }
        return  usuarioRepository.save(usuario.get());
    }

    /**
     * Cambia la contraseña verificando:
     * - Usuario existe y está activo
     * - La contraseña actual coincide
     * - Guarda la nueva hasheada
     */
    @Transactional
    public Usuario cambiarPassword(String username, String passwordActual, String passwordNueva) {
        if (username== null || !StringUtils.hasText(passwordActual) || !StringUtils.hasText(passwordNueva)) {
            throw new IllegalArgumentException("Parámetros inválidos");
        }
        Usuario usuario = usuarioRepository.findUsuarioByUsername(username)
                .orElseThrow(() -> new IllegalStateException("El usuario no existe"));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario está bloqueado. No es posible cambiar la contraseña.");
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getContra())) {
            throw new IllegalStateException("La contraseña actual no es correcta");
        }

        usuario.setContra(passwordEncoder.encode(passwordNueva));
        return usuarioRepository.save(usuario);
    }

    /** Valida si la contraseña proporcionada coincide con el hash del usuario */
    public boolean validarContrasena(String username, String password){
        Optional<Usuario> usuario = usuarioRepository.findUsuarioByUsername(username);
        return usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getContra());
    }
}
