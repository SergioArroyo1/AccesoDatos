package com.example.RA3_Escuela.WebController;

import com.example.RA3_Escuela.DTO.UsuarioDTO;
import com.example.RA3_Escuela.Entity.Roles;
import com.example.RA3_Escuela.Entity.Usuario;
import com.example.RA3_Escuela.Service.RolesService;
import com.example.RA3_Escuela.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
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
class AdminController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolesService rolesService;

    @ResponseBody
    @RequestMapping("/verusuarios")
    public ResponseEntity<?> mostrarUsuarios(HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if(usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try{
            List<Usuario> lista = usuarioService.mostrarUsuarios();

            List<UsuarioDTO> lista2 = new ArrayList<>();

            for (Usuario usuario : lista){
                String rol = usuario.getRoles().iterator().next().getNombreRol();
                UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getEmail(), usuario.getNombre(),rol, usuario.getFechaCreacion(), usuario.isActivo());
                lista2.add(usuarioDTO);
            }
            return ResponseEntity.ok(lista2);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("No hay usuarios que mostrar");
        }

    }

    @ResponseBody
    @DeleteMapping("/eliminarusuario/{id}")
    public ResponseEntity<?> elimnarUsuario(@PathVariable int id, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if(usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminar" + id);
    }

    @ResponseBody
    @PutMapping("/editarusuario")

    public ResponseEntity<?>actualizarUsuario(@RequestBody UsuarioDTO datosUsuario, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"admin".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(datosUsuario.getId());
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no existe");
        }

        usuario.get().setNombre(datosUsuario.getNombre());
        usuario.get().setEmail(datosUsuario.getEmail());

        if (datosUsuario.getUsername() != null && !datosUsuario.getUsername().isEmpty()) {
            usuario.get().setUsername(datosUsuario.getUsername());
        }

        usuario.get().setActivo(datosUsuario.isActivo());

        String rolNombre = datosUsuario.getRol();

        if (rolNombre != null && !rolNombre.isEmpty()) {
            Optional<Roles> rolEncontrado = rolesService.bucarPorNombre(rolNombre);

            if (rolEncontrado.isPresent()) {
                usuario.get().getRoles().clear();

                usuario.get().getRoles().add(rolEncontrado.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error con el rol");
            }
        }
        Usuario usuarioGuardado = usuarioService.actualizarUsuario(usuario.get());
        return ResponseEntity.ok(usuarioGuardado);
    }

}
