package com.example.Ra3.WebController;
import com.example.Ra3.DTO.DepartamentoDTO;
import com.example.Ra3.DTO.UsuarioDTO;
import com.example.Ra3.Modelo.Departamento;
import com.example.Ra3.Service.DepartamentoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador web para gestionar departamentos (solo admin).
 * - Página HTML de gestión
 * - Endpoints JSON para listar/crear/editar/eliminar
 */
@Controller
@RequestMapping("/departamentos")
public class DepartamentoWebController {

    @Autowired
    private DepartamentoService departamentoService;

    /**
     * GET /departamentos
     * Devuelve la página de gestión de departamentos (solo admin).
     */
    @GetMapping("")
    public String paginaDepartamentos(HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null) return "redirect:/killSession";
        if (!"admin".equalsIgnoreCase(u.getRol())) return "redirect:/index.html";
        return "departamentos";
    }

    /**
     * GET /departamentos/lista
     * Devuelve lista de departamentos para construir la UI.
     */
    @ResponseBody
    @GetMapping("/lista")
    public ResponseEntity<List<DepartamentoDTO>> lista(HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(u.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<DepartamentoDTO> data = departamentoService.listar().stream()
                .map(d -> new DepartamentoDTO(d.getId(), d.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    /**
     * POST /departamentos
     * Crea un departamento nuevo.
     */
    @ResponseBody
    @PostMapping("")
    public ResponseEntity<DepartamentoDTO> crear(@RequestBody DepartamentoDTO req, HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(u.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Departamento created = departamentoService.crear(req.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DepartamentoDTO(created.getId(), created.getNombre()));
    }

    /**
     * PUT /departamentos/{id}
     * Actualiza el nombre de un departamento.
     */
    @ResponseBody
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> actualizar(@PathVariable Long id, @RequestBody DepartamentoDTO req, HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(u.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Departamento updated = departamentoService.actualizar(id, req.getNombre());
        return ResponseEntity.ok(new DepartamentoDTO(updated.getId(), updated.getNombre()));
    }

    /**
     * DELETE /departamentos/{id}
     * Intenta eliminar un departamento. Si está en uso por usuarios, devuelve 409.
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!"admin".equalsIgnoreCase(u.getRol())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            departamentoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // en uso por usuarios
        }
    }
}