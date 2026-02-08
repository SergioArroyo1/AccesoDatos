package com.example.RA3_Escuela.WebController;

import com.example.RA3_Escuela.DTO.AlumnoDTO;
import com.example.RA3_Escuela.DTO.UsuarioDTO;
import com.example.RA3_Escuela.Entity.Alumno;
import com.example.RA3_Escuela.Service.AlumnoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recepcion")
class RecepcionController {
    @Autowired
    AlumnoService alumnoService;

    @ResponseBody
    @RequestMapping("/veralumnos")

    public ResponseEntity<?> mostrarAlumnos(HttpSession session){
        UsuarioDTO usuarioSesion = (UsuarioDTO) session.getAttribute("usuarioLogeado");
        if(usuarioSesion == null || !"recepcion".equals((usuarioSesion.getRol()))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            List<Alumno> lista = alumnoService.mostrarAlumnos();
            List<AlumnoDTO> lista2 = new ArrayList<>();
            for(Alumno alumno : lista){
                AlumnoDTO alumnoDTO = new AlumnoDTO(alumno.getId(),alumno.getNombre(), alumno.getApellidos(), alumno.getDni(), alumno.getCurso(), alumno.getFecha_nacimiento(), alumno.getObservaciones(),alumno.isActivo());
                lista2.add(alumnoDTO);
            }
            return ResponseEntity.ok(lista2);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No hay alumnos que mostrar");
        }
    }
}
