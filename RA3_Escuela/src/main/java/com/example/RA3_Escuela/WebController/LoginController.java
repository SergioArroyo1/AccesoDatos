package com.example.RA3_Escuela.WebController;

import com.example.RA3_Escuela.DTO.LoginDTO;
import com.example.RA3_Escuela.DTO.UsuarioDTO;
import com.example.RA3_Escuela.Entity.Usuario;
import com.example.RA3_Escuela.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
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

    public ResponseEntity<?>inicioSesion(@RequestBody LoginDTO loginDTO, HttpSession session){
        if(loginDTO == null){
            return  ResponseEntity.badRequest().body("No puede estar vacio");
        }
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorUsername(loginDTO.getUsername());
        if(usuario.isEmpty()){
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        if(usuarioService.comprobarIniciarSesion(loginDTO)) {
            String rol = usuario.get().getRoles().iterator().next().getNombreRol();
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.get().getId(), usuario.get().getUsername(), usuario.get().getEmail(), usuario.get().getNombre(), rol, usuario.get().getFechaCreacion(), usuario.get().isActivo());

            session.setAttribute("usuarioLogeado", usuarioDTO);
            return ResponseEntity.ok(usuarioDTO);
        }else {
            return ResponseEntity.badRequest().body("No coinciden el usuario ni la contraseña");
        }
    }

    @RequestMapping("/control")
    public String controlAcceso(HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if(usuarioSesion.getRol().equalsIgnoreCase("admin")){
            return "admin/admin";

        }else if(usuarioSesion.getRol().equalsIgnoreCase("profesor")){
            return "profesor/profesor";
        }else if(usuarioSesion.getRol().equalsIgnoreCase("recepcion")){
            return "recepcion/recepcion";
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
