package com.example.RA3_final.Service;

import com.example.RA3_final.Entity.Pacientes;
import com.example.RA3_final.Repository.PacientesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {
    @Autowired
    PacientesRepository pacientesRepository;

    public List<Pacientes> mostrarPacientesPorId(int id) {
        return pacientesRepository.findAllByUsuario_Id(id);
    }

    public List<Pacientes> mostrarPacientes(){
        return pacientesRepository.findAll();
    }

    // Actualizar paciente (corregido)
    @Transactional
    public Pacientes actualizarPaciente(Pacientes pacientes) {
        if (pacientes == null) throw new IllegalArgumentException("Paciente nulo");

        // Comprobación correcta: ¿existe el paciente por su id?
        if (!pacientesRepository.existsById(pacientes.getId())) {
            throw new IllegalStateException("El paciente no existe");
        }

        return pacientesRepository.save(pacientes);
    }

    // Eliminar paciente
    @Transactional
    public void eliminarPaciente(int id) {
        if (!pacientesRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        pacientesRepository.deleteById(id);
    }
}