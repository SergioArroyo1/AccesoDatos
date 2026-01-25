package com.example.Ra3.WebController;


import com.example.Ra3.DTO.RegistroDTO;
import com.example.Ra3.DTO.UsuarioDTO;
import com.example.Ra3.Modelo.Rol;
import com.example.Ra3.Modelo.Usuario;
import com.example.Ra3.Repository.UsuarioRepository;
import com.example.Ra3.Service.RolService;
import com.example.Ra3.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
/**
 * Controlador para registro de nuevos usuarios (rol 'user' por defecto).
 * - GET /registro: devuelve la página HTML
 * - POST /registro: crea el usuario con validaciones y encriptación de password
 */
@Controller
public class RegistroWebController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Página HTML de registro */
    @GetMapping("/registro")
    public String registroPage() {
        return "register"; // templates/register.html
    }

    /** Registro de usuario (JSON) */
    @ResponseBody
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody RegistroDTO dto) {
        // Validaciones básicas de presencia
        if (dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim();

        // Validación de formato y longitud mínima de password
        if (!isValidUsername(username) || !isValidEmail(email) || dto.getPassword().length() < 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Unicidad de username y email
        if (usuarioRepository.existsByUsername(username) || usuarioRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Rol 'user' por defecto (debe existir en BD)
        Optional<Rol> rolUserOpt = rolService.buscarPorNombre("user");
        if (rolUserOpt.isEmpty()) {
            // Si no existe, devuelve error o créalo previamente (seeding)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Rol rolUser = rolUserOpt.get();

        // Crear y guardar usuario con password encriptada
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setContra(passwordEncoder.encode(dto.getPassword()));
        u.setActivo(true);
        u.setFechaCreacion(LocalDateTime.now());

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        u.setRoles(roles);

        Usuario saved = usuarioRepository.save(u);

        String rolFinal = saved.getRoles().iterator().next().getNombre();
        UsuarioDTO out = new UsuarioDTO(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.isActivo(),
                rolFinal,
                saved.getFechaCreacion()
        );

        // 201 Created con el DTO resultante
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    /** Helper para validar email (formato básico) */
    private static boolean isValidEmail(String email) {
        String regex = "^[\\w\\.-]+@[\\w\\.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    /** Helper para validar username (alfa-numérico y _.-, mínimo 3) */
    private static boolean isValidUsername(String username) {
        String regex = "^[A-Za-z0-9_.-]{3,}$";
        return Pattern.compile(regex).matcher(username).matches();
    }
}