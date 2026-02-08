package com.example.RA3_final.WebController;

import com.example.RA3_final.DTO.LoginDTO;
import com.example.RA3_final.DTO.UsuarioDTO;
import com.example.RA3_final.Entity.Usuario;
import com.example.RA3_final.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class LoginController {
    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/comprobar")
    @ResponseBody
    public ResponseEntity<?> inicioSesion(@RequestBody LoginDTO logindto, HttpSession session) {

        if (logindto == null) {
            return ResponseEntity.badRequest().body("No puede estar vacio");
        }
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorUsername(logindto.getUsername());
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no existe");

        }
        if (usuarioService.comprobarInicioSesion(logindto)) {
            String rol = usuario.get().getRoles().iterator().next().getNombreRol();
            UsuarioDTO usuariodto = new UsuarioDTO(usuario.get().getId(), usuario.get().getUsername(), usuario.get().getEmail(), usuario.get().getNombre(), rol, usuario.get().getFechaCreacion(), usuario.get().isActivo());

            session.setAttribute("usuarioLogeado", usuariodto);
            return ResponseEntity.ok(usuariodto);
        } else {
            return ResponseEntity.badRequest().body("No coinciden el usuario y la contrasena");
        }
    }

    @RequestMapping("/control")
    public String controlAcceso(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion.getRol().equalsIgnoreCase("admin")) {
            return "admin/admin";
        } else if (usuarioSesion.getRol().equalsIgnoreCase("medico")) {
            return "medico/medico";

        } else if (usuarioSesion.getRol().equalsIgnoreCase("recepcion")) {

            return "recepcionista/recepcionista";
        } else {
            return "redirect:/killSession";
        }
    }

    @GetMapping("/killSession")
    public String matarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }
}
