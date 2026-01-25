package com.example.Ra3.WebController;

import com.example.Ra3.DTO.LoginDTO;
import com.example.Ra3.DTO.UsuarioDTO;
import com.example.Ra3.Modelo.Usuario;
import com.example.Ra3.Repository.UsuarioRepository;
import com.example.Ra3.Service.RolService;
import com.example.Ra3.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;
/**
 * Controlador de login/logout vía peticiones AJAX.
 */
@Controller
public class LoginWebController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /loginweb
     * - Recibe credenciales
     * - Verifica contraseña
     * - Construye UsuarioDTO con datos esenciales y lo guarda en sesión (usuarioLogeado)
     */
    @PostMapping("/loginweb")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> inicioSesion(@RequestBody LoginDTO loginDTO, HttpSession session){
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorUsername(loginDTO.getUsername());
        if(usuarioOptional.isPresent()){
            Usuario u = usuarioOptional.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), u.getContra())){
                String rol = u.getRoles().iterator().next().getNombre();

                // DTO con datos mínimos para la sesión y la UI
                UsuarioDTO infoLogeado = new UsuarioDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),                 // email
                        u.isActivo(),         // estado
                        rol,                  // rol
                        u.getFechaCreacion()
                );

                // Guardar en sesión: usado para saber si está autenticado y su rol
                session.setAttribute("usuarioLogeado", infoLogeado);
                return ResponseEntity.ok(infoLogeado);
            }
        }
        // Credenciales incorrectas -> 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * GET /killSession
     * - Invalida la sesión y redirige al index (login)
     */
    @GetMapping("/killSession")
    public String matarSesion(HttpSession session){
        session.invalidate();
        return "redirect:/index.html";
    }
}