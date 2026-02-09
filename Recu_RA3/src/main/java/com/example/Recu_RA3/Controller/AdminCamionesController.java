package com.example.Recu_RA3.Controller;

import com.example.Recu_RA3.DTO.CamionesDTO;
import com.example.Recu_RA3.DTO.UsuarioDTO;
import com.example.Recu_RA3.Entity.Camiones;
import com.example.Recu_RA3.Service.CamionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/camiones")
public class AdminCamionesController {

    @Autowired
    private CamionService camionService;

    private boolean isAdmin(UsuarioDTO usuarioSesion) {
        return usuarioSesion != null
                && usuarioSesion.getRol() != null
                && "admin".equalsIgnoreCase(usuarioSesion.getRol());
    }

    @ResponseBody
    @GetMapping("/ver")
    public ResponseEntity<?> verCamiones(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdmin(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<Camiones> lista = camionService.mostrarCamiones(); // todos
            List<CamionesDTO> dto = new ArrayList<>();
            for (Camiones c : lista) {
                dto.add(new CamionesDTO(
                        c.getId(),
                        c.getMatricula(),
                        c.getModelo(),
                        c.getCapacidad_kg(),   // Entity usa capacidad_kg
                        c.getEstado(),
                        c.getFechaAlta(),
                        c.isActivo()
                ));
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>()); // 200 lista vacía
        }
    }

    @ResponseBody
    @PutMapping("/editar")
    public ResponseEntity<?> editarCamion(@RequestBody CamionesDTO datos, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdmin(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Camiones camion = camionService.buscarPorId(datos.getId()).orElse(null);
        if (camion == null) {
            return ResponseEntity.badRequest().body("Camion no existe");
        }

        camion.setMatricula(datos.getMatricula());
        camion.setModelo(datos.getModelo());
        camion.setCapacidad_kg(datos.getCapacidadKg()); // DTO usa capacidadKg
        camion.setEstado(datos.getEstado());
        camion.setFechaAlta(datos.getFechaAlta());
        camion.setActivo(datos.isActivo());

        Camiones guardado = camionService.actualizarCamion(camion);
        return ResponseEntity.ok(guardado);
    }


    @ResponseBody
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCamion(@PathVariable int id, HttpSession session) {
        UsuarioDTO u = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (u == null || !"admin".equalsIgnoreCase(u.getRol())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            camionService.eliminarCamionConDependencias(id);
            return ResponseEntity.ok("Camión eliminado: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo eliminar el camión. Revisa asignaciones o dependencias.");
        }
    }
}