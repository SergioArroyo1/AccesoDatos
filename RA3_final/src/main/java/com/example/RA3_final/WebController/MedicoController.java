package com.example.RA3_final.WebController;

import com.example.RA3_final.DTO.PacienteDTO;
import com.example.RA3_final.DTO.UsuarioDTO;
import com.example.RA3_final.Entity.Pacientes;
import com.example.RA3_final.Entity.Usuario;
import com.example.RA3_final.Service.PacienteService;
import com.example.RA3_final.Service.RolesService;
import com.example.RA3_final.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/medico" )
public class MedicoController {

    @Autowired
    PacienteService pacienteService;
    @Autowired
    RolesService rolesService;

    @ResponseBody
    @RequestMapping("/verpacientes")
    public ResponseEntity<?> mostrarPacientes(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"medico".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
           List<Pacientes> lista = pacienteService.mostrarPacientesPorId(usuarioSesion.getId());
            List<PacienteDTO> lista2 = new ArrayList<>();
           for(Pacientes pacientes : lista){
                PacienteDTO pacientedto = new PacienteDTO(pacientes.getId(),pacientes .getNombre(), pacientes.getApellidos(), pacientes.getDni(), pacientes.getTelefono(),  pacientes.getFechaNacimiento(), pacientes.getHistorial(),pacientes.isActivo());
            lista2.add(pacientedto);
            }
            return ResponseEntity.ok(lista2);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay pacientes que mostrar");

            }
        }
    @ResponseBody
    @DeleteMapping("/eliminarpaciente/{id}")
    public ResponseEntity<?> eliminarPaciente(@PathVariable int id, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"medico".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        pacienteService.eliminarPaciente(id);
        return ResponseEntity.ok("Paciente eliminado: " + id);
    }

    @ResponseBody
    @PutMapping("/editarpaciente")
    public ResponseEntity<?> actualizarPaciente(@RequestBody PacienteDTO datosPaciente, HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"medico".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Pacientes> misPacientes = pacienteService.mostrarPacientesPorId(usuarioSesion.getId());
        Pacientes paciente = misPacientes.stream()
                .filter(p -> p.getId() == datosPaciente.getId())
                .findFirst()
                .orElse(null);

        if (paciente == null) {
            return ResponseEntity.badRequest().body("Paciente no existe o no pertenece al médico");
        }

        // Actualizamos los campos del paciente
        paciente.setNombre(datosPaciente.getNombre());
        paciente.setApellidos(datosPaciente.getApellidos());
        paciente.setDni(datosPaciente.getDni());
        paciente.setTelefono(datosPaciente.getTelefono());
        paciente.setFechaNacimiento(datosPaciente.getFechaNacimiento());
        paciente.setHistorial(datosPaciente.getHistorial());
        paciente.setActivo(datosPaciente.isActivo());

        // Guardamos cambios
        Pacientes pacienteGuardado = pacienteService.actualizarPaciente(paciente);

        return ResponseEntity.ok(pacienteGuardado);
    }


    }


