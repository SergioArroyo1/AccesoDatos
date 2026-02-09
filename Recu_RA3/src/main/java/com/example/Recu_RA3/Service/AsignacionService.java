package com.example.Recu_RA3.Service;

import com.example.Recu_RA3.Entity.Asignacion;
import com.example.Recu_RA3.Entity.Camiones;
import com.example.Recu_RA3.Entity.Ruta;
import com.example.Recu_RA3.Repository.AsignacionRepository;
import com.example.Recu_RA3.Repository.CamionesRepository;
import com.example.Recu_RA3.Repository.RutaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private CamionesRepository camionesRepository;

    @Autowired
    private RutaRepository rutaRepository;

    public List<Asignacion> mostrarAsignaciones() {
        return asignacionRepository.findAll();
    }

    public List<Asignacion> buscarPorCamionId(int camionId) {
        return asignacionRepository.findByCamionId(camionId);
    }

    public List<Asignacion> buscarPorRutaId(Long rutaId) {
        return asignacionRepository.findByRutaId(rutaId);
    }

    public Optional<Asignacion> buscarPorId(Long id) {
        return asignacionRepository.findById(id);
    }

    @Transactional
    public Asignacion crearAsignacion(int camionId, Long rutaId) {
        Optional<Camiones> camion = camionesRepository.findById(camionId);
        Optional<Ruta> ruta = rutaRepository.findById(rutaId);

        if (camion.isEmpty()) {
            throw new IllegalArgumentException("Camión no encontrado con ID: " + camionId);
        }
        if (ruta.isEmpty()) {
            throw new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId);
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setCamion(camion.get());
        asignacion.setRuta(ruta.get());
        asignacion.setFechaAsignacion(LocalDate.now());

        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void eliminarAsignacion(Long id) {
        if (!asignacionRepository.existsById(id)) {
            throw new IllegalArgumentException("Asignación no encontrada con ID: " + id);
        }
        asignacionRepository.deleteById(id);
    }
}