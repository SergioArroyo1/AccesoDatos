package com.example.RA3_Escuela.WebController;

import com.example.RA3_Escuela.DTO.AlumnoDTO;
import com.example.RA3_Escuela.DTO.UsuarioDTO;
import com.example.RA3_Escuela.Entity.Alumno;
import com.example.RA3_Escuela.Service.AlumnoService;
import com.example.RA3_Escuela.Service.RolesService;
import com.example.RA3_Escuela.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolesService rolesService;
    @Autowired
    AlumnoService alumnoService;

    @ResponseBody
    @RequestMapping("/veralumnos")
    public ResponseEntity<?> mostrarAlumnos(HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"profesor".equals((usuarioSesion.getRol()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        try {
            List<Alumno> lista = alumnoService.mostrarAlumnosPorId(usuarioSesion.getId());
            List<AlumnoDTO> lista2 = new ArrayList<>();
            for (Alumno alumno : lista) {
                AlumnoDTO alumnoDTO = new AlumnoDTO(alumno.getId(), alumno.getNombre(), alumno.getApellidos(), alumno.getDni(), alumno.getCurso(), alumno.getFecha_nacimiento(), alumno.getObservaciones(), alumno.isActivo());
                lista2.add(alumnoDTO);
            }
            return ResponseEntity.ok(lista2);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay alumnos que mostrar");

        }
    }

    @ResponseBody
    @DeleteMapping("/eliminaralumno/{id}")
    public ResponseEntity<?> eliminarAlumno(@PathVariable int id, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"profesor".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        alumnoService.eliminarAlumno(id);
        return ResponseEntity.ok("Alumno eliminado: " + id);
    }

    @ResponseBody
    @PutMapping("/editaralumno")
    public ResponseEntity<?> actualizarPaciente(@RequestBody AlumnoDTO datosAlumno, HttpSession session) {
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if (usuarioSesion == null || !"profesor".equals(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Alumno> misAlumnos = alumnoService.mostrarAlumnosPorId(usuarioSesion.getId());
        Alumno alumno = misAlumnos.stream()
                .filter(p -> p.getId() == datosAlumno.getId())
                .findFirst()
                .orElse(null);

        if (alumno == null) {
            return ResponseEntity.badRequest().body("Paciente no existe o no pertenece al médico");
        }

        // Actualizamos los campos del paciente
        alumno.setNombre(datosAlumno.getNombre());
        alumno.setApellidos(datosAlumno.getApellidos());
        alumno.setDni(datosAlumno.getDni());
        alumno.setCurso(datosAlumno.getCurso());
        alumno.setFecha_nacimiento(datosAlumno.getFechaNacimiento());
        alumno.setObservaciones(datosAlumno.getObservaciones());
        alumno.setActivo(datosAlumno.isActivo());

        // Guardamos cambios
        Alumno alumnoGuardado = alumnoService.actualizarAlumno(alumno);

        return ResponseEntity.ok(alumnoGuardado);
    }
}