package com.example.Recu_RA3.Controller;

import com.example.Recu_RA3.DTO.UsuarioDTO;
import com.example.Recu_RA3.Entity.Ruta;
import com.example.Recu_RA3.Service.RutaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rutas")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    private boolean isAdminOrCoord(UsuarioDTO usuarioSesion) {
        if (usuarioSesion == null || usuarioSesion.getRol() == null) return false;
        String rol = usuarioSesion.getRol();
        return rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("coordinador");
    }

    @ResponseBody
    @GetMapping("/ver")
    public ResponseEntity<?> mostrarRutas(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Ruta> rutas = rutaService.mostrarRutas();
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay rutas que mostrar");
        }
    }

    @ResponseBody
    @GetMapping("/activas")
    public ResponseEntity<?> mostrarRutasActivas(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Ruta> rutas = rutaService.mostrarRutasActivas();
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay rutas activas");
        }
    }

    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarRutaPorId(@PathVariable Long id, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return rutaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @PostMapping("/crear")
    public ResponseEntity<?> crearRuta(@RequestBody Ruta ruta, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Ruta rutaCreada = rutaService.crearRuta(ruta);
            return ResponseEntity.ok(rutaCreada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear ruta: " + e.getMessage());
        }
    }

    @ResponseBody
    @PutMapping("/editar")
    public ResponseEntity<?> actualizarRuta(@RequestBody Ruta ruta, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Ruta rutaActualizada = rutaService.actualizarRuta(ruta);
            return ResponseEntity.ok(rutaActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar ruta: " + e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable Long id, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            rutaService.eliminarRuta(id);
            return ResponseEntity.ok("Ruta eliminada: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar ruta: " + e.getMessage());
        }
    }
}