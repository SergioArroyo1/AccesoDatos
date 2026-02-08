package com.example.RA3_final.WebController;


import com.example.RA3_final.DTO.PacienteDTO;
import com.example.RA3_final.DTO.UsuarioDTO;
import com.example.RA3_final.Entity.Pacientes;
import com.example.RA3_final.Service.PacienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recepcionista" )
public class RecepcionistaController {
    @Autowired
    PacienteService pacienteService;
    @ResponseBody
    @RequestMapping("/verpacientes")

    public ResponseEntity<?> mostrarPacientes(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"recepcion".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<Pacientes> lista = pacienteService.mostrarPacientes();
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
}
