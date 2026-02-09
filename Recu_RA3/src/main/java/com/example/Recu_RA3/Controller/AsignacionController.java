package com.example.Recu_RA3.Controller;

import com.example.Recu_RA3.DTO.UsuarioDTO;
import com.example.Recu_RA3.Entity.Asignacion;
import com.example.Recu_RA3.Service.AsignacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    private boolean isAdminOrCoord(UsuarioDTO usuarioSesion) {
        if (usuarioSesion == null || usuarioSesion.getRol() == null) return false;
        String rol = usuarioSesion.getRol();
        return rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("coordinador");
    }

    @ResponseBody
    @GetMapping("/ver")
    public ResponseEntity<?> mostrarAsignaciones(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || usuarioSesion.getRol() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<Asignacion> asignaciones = asignacionService.mostrarAsignaciones();
            // Devuelve siempre 200 con la lista (vacía si no hay)
            return ResponseEntity.ok(asignaciones);
        } catch (Exception e) {
            // En caso de error controlado, mejor devolver lista vacía a 200
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @ResponseBody
    @GetMapping("/camion/{camionId}")
    public ResponseEntity<?> buscarPorCamion(@PathVariable int camionId, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Asignacion> asignaciones = asignacionService.buscarPorCamionId(camionId);
            return ResponseEntity.ok(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al buscar asignaciones del camión");
        }
    }

    @ResponseBody
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<?> buscarPorRuta(@PathVariable Long rutaId, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Asignacion> asignaciones = asignacionService.buscarPorRutaId(rutaId);
            return ResponseEntity.ok(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al buscar asignaciones de la ruta");
        }
    }

    @ResponseBody
    @PostMapping("/crear")
    public ResponseEntity<?> crearAsignacion(@RequestBody Map<String, Object> datos, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            int camionId = (int) datos.get("camionId");
            Long rutaId = Long.valueOf(datos.get("rutaId").toString());

            Asignacion asignacion = asignacionService.crearAsignacion(camionId, rutaId);
            return ResponseEntity.ok(asignacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear asignación: " + e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarAsignacion(@PathVariable Long id, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            asignacionService.eliminarAsignacion(id);
            return ResponseEntity.ok("Asignación eliminada: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar asignación: " + e.getMessage());
        }
    }
}