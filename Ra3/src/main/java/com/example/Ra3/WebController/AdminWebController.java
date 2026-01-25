package com.example.Ra3.WebController;


import com.example.Ra3.DTO.UsuarioDTO;
import com.example.Ra3.DTO.UsuarioUpdateRequest;
import com.example.Ra3.DTO.UsuarioViewDTO;
import com.example.Ra3.Modelo.Departamento;
import com.example.Ra3.Modelo.Rol;
import com.example.Ra3.Modelo.Usuario;
import com.example.Ra3.Repository.DepartamentoRepository;
import com.example.Ra3.Repository.UsuarioRepository;
import com.example.Ra3.Service.RolService;
import com.example.Ra3.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
/**
 * Controlador web para endpoints del panel administrador.
 * - Muestra datos del admin
 * - Lista y actualiza usuarios
 * - Elimina usuarios
 */
@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolService rolService;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    DepartamentoRepository departamentoRepository;

    /**
     * GET /admin
     * Devuelve datos del admin autenticado (desde sesión).
     */
    @ResponseBody
    @GetMapping
    public ResponseEntity<UsuarioDTO> mostrarDatosAdmin(HttpSession session){
        UsuarioDTO usuarioSesionDTO = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesionDTO == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return usuarioService.buscarPorUsername(usuarioSesionDTO.getUsername())
                .map(u -> {
                    String rol = u.getRoles().iterator().next().getNombre();
                    return ResponseEntity.ok(
                            new UsuarioDTO(u.getId(), u.getUsername(), u.getEmail(), u.isActivo(), rol, u.getFechaCreacion())
                    );
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * GET /admin/verUsuarios
     * Lista todos los usuarios con datos enriquecidos (incluye departamento).
     * Solo disponible para rol admin.
     */
    @ResponseBody
    @GetMapping("/verUsuarios")
    public ResponseEntity<List<UsuarioViewDTO>> mostrarUsuarios(HttpSession session){
        UsuarioDTO usuarioSesionDTO = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesionDTO == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(usuarioSesionDTO.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<Usuario> lista = usuarioService.mostrarUsuario();
        List<UsuarioViewDTO> listaDTO = new ArrayList<>();
        for (Usuario usuario : lista){
            String rol = usuario.getRoles().iterator().next().getNombre();
            Long depId = usuario.getDepartamento() != null ? usuario.getDepartamento().getId() : null;
            String depNom = usuario.getDepartamento() != null ? usuario.getDepartamento().getNombre() : null;
            listaDTO.add(new UsuarioViewDTO(
                    usuario.getId(), usuario.getUsername(), usuario.getEmail(),
                    usuario.isActivo(), rol, usuario.getFechaCreacion(),
                    depId, depNom
            ));
        }
        return ResponseEntity.ok(listaDTO);
    }

    /**
     * PUT /admin/usuarios/{id}
     * Actualiza los datos de un usuario:
     * - username, estado, rol, email, departamento
     * Validaciones:
     * - Autenticación y rol admin
     * - Email válido y único si cambia
     * - Rol existente
     */
    @ResponseBody
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioViewDTO> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateRequest update,
            HttpSession session
    ) {
        UsuarioDTO usuarioSesionDTO = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesionDTO == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(usuarioSesionDTO.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Usuario> optUsuario = usuarioService.buscarPorId(id);
        if (optUsuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Usuario usuario = optUsuario.get();

        // username
        if (update.getUsername() != null && !update.getUsername().isBlank()) {
            usuario.setUsername(update.getUsername().trim());
        }

        // estado
        usuario.setActivo(update.isEstado());

        // rol
        if (update.getRol() != null && !update.getRol().isBlank()) {
            String nuevoRolNombre = update.getRol().trim().toLowerCase();
            Optional<Rol> rolOpt = rolService.buscarPorNombre(nuevoRolNombre);
            if (rolOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            Set<Rol> roles = new HashSet<>();
            roles.add(rolOpt.get());
            usuario.setRoles(roles);
        }

        // email
        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            String nuevoEmail = update.getEmail().trim();
            if (!isValidEmail(nuevoEmail)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            if (!nuevoEmail.equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(nuevoEmail))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            usuario.setEmail(nuevoEmail);
        }

        // departamento (puede ser null para quitar asignación)
        if (update.getDepartamentoId() != null) {
            Departamento dep = departamentoRepository.findById(update.getDepartamentoId())
                    .orElse(null);
            usuario.setDepartamento(dep);
        }

        Usuario guardado = usuarioRepository.save(usuario);
        String rolFinal = guardado.getRoles().iterator().next().getNombre();
        Long depId = guardado.getDepartamento() != null ? guardado.getDepartamento().getId() : null;
        String depNom = guardado.getDepartamento() != null ? guardado.getDepartamento().getNombre() : null;

        UsuarioViewDTO dto = new UsuarioViewDTO(
                guardado.getId(), guardado.getUsername(), guardado.getEmail(),
                guardado.isActivo(), rolFinal, guardado.getFechaCreacion(),
                depId, depNom
        );
        return ResponseEntity.ok(dto);
    }

    /** Helper de validación de email (formato básico) */
    private static boolean isValidEmail(String email) {
        String regex = "^[\\w\\.-]+@[\\w\\.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(regex);
    }

    /**
     * DELETE /admin/usuarios/{id}
     * Elimina un usuario por ID (solo admin).
     * - Impide que el admin se borre a sí mismo (409) de forma opcional.
     */
    @ResponseBody
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, HttpSession session) {
        UsuarioDTO usuarioSesionDTO = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesionDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!"admin".equalsIgnoreCase(usuarioSesionDTO.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Usuario> optUsuario = usuarioService.buscarPorId(id);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Evita que el admin se elimine a sí mismo por accidente (opcional)
        if (Objects.equals(usuarioSesionDTO.getId(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }
}