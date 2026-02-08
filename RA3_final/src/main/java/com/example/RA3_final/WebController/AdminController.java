package com.example.RA3_final.WebController;


import com.example.RA3_final.DTO.UsuarioDTO;
import com.example.RA3_final.Entity.Roles;
import com.example.RA3_final.Entity.Usuario;
import com.example.RA3_final.Service.RolesService;
import com.example.RA3_final.Service.UsuarioService;
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
@RequestMapping("/admin" )
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
                String rol = usuario.getRoles().iterator().next().getNombreRol();
                UsuarioDTO usuariodto = new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getEmail(), usuario.getNombre(), rol, usuario.getFechaCreacion(), usuario.isActivo());
                lista2.add(usuariodto);
            }
           return ResponseEntity.ok(lista2);

        }catch (Exception e){
            return ResponseEntity.badRequest().body("No hay usuarios que mostrar");

        }
    }
    @ResponseBody
    @DeleteMapping("/eliminarusuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        usuarioService.eliminarUsuario(id);
        return  ResponseEntity.ok("Usuario eliminar" + id);
    }
    @ResponseBody
    @PutMapping("/editarusuario")
    public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioDTO datosUsuario, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(datosUsuario.getId());
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no existe");

        }
        // 2. Actualizamos campos básicos
        usuario.get().setNombre(datosUsuario.getNombre());
        usuario.get().setEmail(datosUsuario.getEmail());

        if (datosUsuario.getUsername() != null && !datosUsuario.getUsername().isEmpty()) {
            usuario.get().setUsername(datosUsuario.getUsername());
        }

        usuario.get().setActivo(datosUsuario.isActivo());

        // 3. ACTUALIZACIÓN DEL ROL (La parte clave)
        String rolNombre = datosUsuario.getRol(); // Viene "ADMIN" o "USER"

        if (rolNombre != null && !rolNombre.isEmpty()) {
            // Buscamos el objeto Rol en la base de datos
            Optional<Roles> rolEncontrado = rolesService.buscarPorNombre(rolNombre);

            if (rolEncontrado.isPresent()) {
                // LIMPIAMOS los roles antiguos
                usuario.get().getRoles().clear();

                // AÑADIMOS el nuevo rol
                usuario.get().getRoles().add(rolEncontrado.get());
            } else {
                // Opcional: Si el rol no existe, podrías devolver error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error con el Rol");
            }
        }

        // 4. Guardamos
        Usuario usuarioGuardado = usuarioService.actualizarUsuario(usuario.get());

        return ResponseEntity.ok(usuarioGuardado);


    }

}
