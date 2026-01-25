package com.example.Ra3.WebController;


import com.example.Ra3.DTO.UsuarioDTO;
import com.example.Ra3.DTO.UsuarioViewDTO;
import com.example.Ra3.Modelo.Usuario;
import com.example.Ra3.Repository.UsuarioRepository;
import com.example.Ra3.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controlador para la página de usuario (no admin):
 * - Devuelve sus datos (incluye departamento)
 * - Permite editar username/email propios
 */
@Controller
@RequestMapping("/user")
public class UsuarioWebController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    /**
     * GET /user/datos
     * Devuelve datos del usuario autenticado desde sesión (en formato UsuarioViewDTO).
     */
    @ResponseBody
    @GetMapping("/datos")
    public ResponseEntity<UsuarioViewDTO> mostrarDatos(HttpSession session) {
        UsuarioDTO sesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (sesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(sesion.getId());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Usuario u = usuarioOptional.get();
        String rol = u.getRoles().iterator().next().getNombre();
        Long depId = u.getDepartamento() != null ? u.getDepartamento().getId() : null;
        String depNom = u.getDepartamento() != null ? u.getDepartamento().getNombre() : null;

        UsuarioViewDTO dto = new UsuarioViewDTO(
                u.getId(), u.getUsername(), u.getEmail(),
                u.isActivo(), rol, u.getFechaCreacion(),
                depId, depNom
        );
        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /user/datos
     * Actualiza username y email del usuario autenticado.
     * - Valida formato y unicidad
     * - Actualiza la sesión con nuevos datos
     * - Devuelve UsuarioViewDTO (incluye departamento) para renderizar correctamente la UI
     */
    @ResponseBody
    @PutMapping("/datos")
    @Transactional
    public ResponseEntity<UsuarioViewDTO> actualizarMisDatos(@RequestBody UsuarioDTO update, HttpSession session) {
        UsuarioDTO sesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (sesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Usuario> optUsuario = usuarioService.buscarPorId(sesion.getId());
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Usuario usuario = optUsuario.get();

        // username: validar formato y unicidad si cambia
        if (update.getUsername() != null && !update.getUsername().isBlank()) {
            String nuevoUsername = update.getUsername().trim();
            if (!isValidUsername(nuevoUsername)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            if (!nuevoUsername.equalsIgnoreCase(usuario.getUsername()) &&
                    usuarioRepository.existsByUsername(nuevoUsername)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            usuario.setUsername(nuevoUsername);
        }

        // email: validar formato y unicidad si cambia
        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            String nuevoEmail = update.getEmail().trim();
            if (!isValidEmail(nuevoEmail)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            if (!nuevoEmail.equalsIgnoreCase(usuario.getEmail()) &&
                    usuarioRepository.existsByEmail(nuevoEmail)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            usuario.setEmail(nuevoEmail);
        }

        Usuario guardado = usuarioRepository.save(usuario);

        // Actualiza la sesión (UsuarioDTO) para próximas peticiones
        String rolFinal = guardado.getRoles().iterator().next().getNombre();
        UsuarioDTO sesionActualizada = new UsuarioDTO(
                guardado.getId(), guardado.getUsername(), guardado.getEmail(),
                guardado.isActivo(), rolFinal, guardado.getFechaCreacion()
        );
        session.setAttribute("usuarioLogeado", sesionActualizada);

        // RESPUESTA: UsuarioViewDTO con departamento
        Long depId = guardado.getDepartamento() != null ? guardado.getDepartamento().getId() : null;
        String depNom = guardado.getDepartamento() != null ? guardado.getDepartamento().getNombre() : null;

        UsuarioViewDTO view = new UsuarioViewDTO(
                guardado.getId(), guardado.getUsername(), guardado.getEmail(),
                guardado.isActivo(), rolFinal, guardado.getFechaCreacion(),
                depId, depNom
        );
        return ResponseEntity.ok(view);
    }

    /** Valida formato básico de email */
    private static boolean isValidEmail(String email) {
        String regex = "^[\\w\\.-]+@[\\w\\.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    /** Valida formato básico de username (alfa-numérico y _.-, mínimo 3) */
    private static boolean isValidUsername(String username) {
        String regex = "^[A-Za-z0-9_.-]{3,}$";
        return Pattern.compile(regex).matcher(username).matches();
    }
}