package com.example.Recu_RA3.Controller;

import com.example.Recu_RA3.DTO.UsuarioDTO;
import com.example.Recu_RA3.Entity.Roles;
import com.example.Recu_RA3.Entity.Usuario;
import com.example.Recu_RA3.Service.RolesService;
import com.example.Recu_RA3.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolesService rolesService;

    @ResponseBody
    @RequestMapping("/verusuarios")
    public ResponseEntity<?> mostrarUsuarios(HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            List<Usuario> lista = usuarioService.mostrarusuarios();

            List<UsuarioDTO> lista2 = new ArrayList<>();
            for(Usuario usuario : lista){
                // ← VALIDACIÓN: Verificar que el usuario tenga roles
                String rol = "sin_rol"; // valor por defecto
                if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                    rol = usuario.getRoles().iterator().next().getNombreRol();
                }

                UsuarioDTO usuariodto = new UsuarioDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getNombre(),
                        rol,
                        usuario.getFechaCreacion(),
                        usuario.isActivo()
                );
                lista2.add(usuariodto);
            }
            return ResponseEntity.ok(lista2);

        }catch (Exception e){
            e.printStackTrace(); // Ver el error en consola
            return ResponseEntity.badRequest().body("Error al cargar usuarios: " + e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/eliminarusuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @ResponseBody
    @PutMapping("/editarusuario")
    public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioDTO datosUsuario, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(datosUsuario.getId());
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no existe");
            }

            Usuario usuario = usuarioOpt.get();

            // 2. Actualizamos campos básicos
            usuario.setNombre(datosUsuario.getNombre());
            usuario.setEmail(datosUsuario.getEmail());

            if (datosUsuario.getUsername() != null && !datosUsuario.getUsername().isEmpty()) {
                usuario.setUsername(datosUsuario.getUsername());
            }

            usuario.setActivo(datosUsuario.isActivo());

            // 3. ACTUALIZACIÓN DEL ROL (La parte clave)
            String rolNombre = datosUsuario.getRol();

            if (rolNombre != null && !rolNombre.isEmpty()) {
                // Buscamos el objeto Rol en la base de datos
                Optional<Roles> rolEncontrado = rolesService.buscarPorNombre(rolNombre);

                if (rolEncontrado.isPresent()) {
                    // LIMPIAMOS los roles antiguos
                    usuario.getRoles().clear();

                    // AÑADIMOS el nuevo rol
                    usuario.getRoles().add(rolEncontrado.get());
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Rol no encontrado");
                }
            }

            // 4. Guardamos
            Usuario usuarioGuardado = usuarioService.actualizarUsuario(usuario);

            return ResponseEntity.ok(usuarioGuardado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }
}