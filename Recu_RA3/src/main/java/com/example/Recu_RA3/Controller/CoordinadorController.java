package com.example.Recu_RA3.Controller;

import com.example.Recu_RA3.DTO.CamionesDTO;
import com.example.Recu_RA3.DTO.UsuarioDTO;
import com.example.Recu_RA3.Entity.Camiones;
import com.example.Recu_RA3.Service.CamionService;
import com.example.Recu_RA3.Service.RolesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/coordinador" )
public class CoordinadorController {

    @Autowired
    CamionService camionService;
    @Autowired
    RolesService rolesService;

    private boolean isAdminOrCoord(UsuarioDTO usuarioSesion) {
        if (usuarioSesion == null || usuarioSesion.getRol() == null) return false;
        String rol = usuarioSesion.getRol();
        return rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("coordinador");
    }

    @ResponseBody
    @RequestMapping("/vercamiones")
    public ResponseEntity<?> mostrarCamiones(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // Si es admin, puede ver todos los camiones; si es coordinador, solo los suyos
            List<Camiones> lista = usuarioSesion.getRol().equalsIgnoreCase("admin")
                    ? camionService.mostrarCamiones()
                    : camionService.mostrarCamionesPorId(usuarioSesion.getId());

            List<CamionesDTO> lista2 = new ArrayList<>();
            for(Camiones camiones : lista){
                CamionesDTO camionesDTO = new CamionesDTO(
                        camiones.getId(),
                        camiones.getMatricula(),
                        camiones.getModelo(),
                        camiones.getCapacidad_kg(),
                        camiones.getEstado(),
                        camiones.getFechaAlta(),
                        camiones.isActivo()
                );
                lista2.add(camionesDTO);
            }
            return ResponseEntity.ok(lista2);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay camiones que mostrar");
        }
    }

    @ResponseBody
    @DeleteMapping("/eliminarcamion/{id}")
    public ResponseEntity<?> eliminarCamion(@PathVariable int id, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Admin puede eliminar cualquiera; coordinador solo los suyos
        if (usuarioSesion.getRol().equalsIgnoreCase("coordinador")) {
            List<Camiones> misCamiones = camionService.mostrarCamionesPorId(usuarioSesion.getId());
            boolean mePertenece = misCamiones.stream().anyMatch(c -> c.getId() == id);
            if (!mePertenece) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar camiones de otros usuarios");
            }
        }

        camionService.eliminarCamionConDependencias(id);
        return ResponseEntity.ok("Camion eliminado: " + id);
    }

    @ResponseBody
    @PutMapping("/editarcamion")
    public ResponseEntity<?> actualizarCamion(@RequestBody CamionesDTO datosCamion, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (!isAdminOrCoord(usuarioSesion)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Admin puede editar cualquiera; coordinador solo los suyos
        List<Camiones> listaObjetivo = usuarioSesion.getRol().equalsIgnoreCase("admin")
                ? camionService.mostrarCamiones()
                : camionService.mostrarCamionesPorId(usuarioSesion.getId());

        Camiones camion = listaObjetivo.stream()
                .filter(p -> p.getId() == datosCamion.getId())
                .findFirst()
                .orElse(null);

        if (camion == null) {
            return ResponseEntity.badRequest().body("Camion no existe o no tienes permiso para editarlo");
        }

        camion.setMatricula(datosCamion.getMatricula());
        camion.setModelo(datosCamion.getModelo());
        camion.setCapacidad_kg(datosCamion.getCapacidadKg());
        camion.setEstado(datosCamion.getEstado());
        camion.setFechaAlta(datosCamion.getFechaAlta());
        camion.setActivo(datosCamion.isActivo());

        Camiones camionGuardado = camionService.actualizarCamion(camion);
        return ResponseEntity.ok(camionGuardado);
    }
}