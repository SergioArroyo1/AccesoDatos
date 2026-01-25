package com.example.Ra3.WebController;


import com.example.Ra3.DTO.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Controla la navegación según el rol almacenado en sesión.
 * - /control redirige al panel admin o usuario
 * - /control/usuarios solo visible para admin (gestión de usuarios)
 */
@Controller
@RequestMapping("/control")
public class SesionWebController {

    /** Redirige a la vista adecuada según rol o a login si no hay sesión */
    @GetMapping("")
    public String controlarSesion(HttpSession sesion) {
        UsuarioDTO user = (UsuarioDTO) sesion.getAttribute("usuarioLogeado");
        if (user == null) {
            return "redirect:/killSession";
        }
        String rol = user.getRol();
        if ("admin".equalsIgnoreCase(rol)) {
            return "admin/admin";   // subcarpeta + nombre
        } else if ("user".equalsIgnoreCase(rol)) {
            return "user/user";     // subcarpeta + nombre
        } else {
            return "redirect:/index.html";
        }
    }

    /** Página de gestión de usuarios (solo admin) */
    @GetMapping("/usuarios")
    public String vistaControlUsuarios(HttpSession sesion) {
        UsuarioDTO user = (UsuarioDTO) sesion.getAttribute("usuarioLogeado");
        if (user != null && "admin".equalsIgnoreCase(user.getRol())) {
            return "control"; // está en templates/control.html
        }
        return "redirect:/index.html";
    }
}