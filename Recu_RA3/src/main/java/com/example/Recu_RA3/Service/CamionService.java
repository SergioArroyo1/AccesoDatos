package com.example.Recu_RA3.Service;


import com.example.Recu_RA3.Entity.Camiones;
import com.example.Recu_RA3.Repository.AsignacionRepository;
import com.example.Recu_RA3.Repository.CamionesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CamionService {
    @Autowired
    CamionesRepository camionesRepository;
    @Autowired
    AsignacionRepository asignacionRepository;

    public List<Camiones> mostrarCamionesPorId(int id) {
        return camionesRepository.findAllByUsuario_Id(id);
    }

    public Optional<Camiones> buscarPorId(int id) {
        return camionesRepository.findById(id);
    }

    public List<Camiones> mostrarCamiones() {return camionesRepository.findAll();}

    // Actualizar paciente (corregido)
    @Transactional
    public Camiones actualizarCamion(Camiones camiones) {
        if (camiones == null) throw new IllegalArgumentException("Camion nulo");

        // Comprobación correcta: ¿existe el paciente por su id?
        if (!camionesRepository.existsById(camiones.getId())) {
            throw new IllegalStateException("El camion no existe");
        }

        return camionesRepository.save(camiones);
    }


    @Transactional
    public void eliminarCamionConDependencias(int id) {
        // 1) borra asignaciones del camión
        asignacionRepository.deleteByCamionId(id);
        // 2) borra el camión
        camionesRepository.deleteById(id);
    }

}