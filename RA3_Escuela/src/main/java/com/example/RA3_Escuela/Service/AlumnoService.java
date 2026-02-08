package com.example.RA3_Escuela.Service;

import com.example.RA3_Escuela.Entity.Alumno;
import com.example.RA3_Escuela.Repository.AlumnosRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {

    @Autowired
    AlumnosRepository alumnosRepository;

    public List<Alumno> mostrarAlumnosPorId(int id) {
        return alumnosRepository.findAllByUsuario_Id(id);
    }

    public List<Alumno> mostrarAlumnos() {
        return alumnosRepository.findAll();
    }

    @Transactional
    public Alumno actualizarAlumno(Alumno alumno) {
        if (alumno == null) throw new IllegalArgumentException("Alumno nulo");

        if (!alumnosRepository.existsById(alumno.getId())) {
            throw new IllegalStateException("El alumno no existe");
        }
        return alumnosRepository.save(alumno);
    }

     @Transactional
    public void eliminarAlumno(int id){
        if(!alumnosRepository.existsById(id)){
            throw new IllegalArgumentException("El alumno no existe con id: " + id);

        }
        alumnosRepository.deleteById(id);
     }



}
