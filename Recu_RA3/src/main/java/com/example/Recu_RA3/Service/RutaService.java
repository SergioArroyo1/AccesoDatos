package com.example.Recu_RA3.Service;

import com.example.Recu_RA3.Entity.Ruta;
import com.example.Recu_RA3.Repository.RutaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RutaService {

    @Autowired
    RutaRepository rutaRepository;

    public List<Ruta> mostrarRutas() {
        return rutaRepository.findAll();
    }

    public List<Ruta> mostrarRutasActivas() {
        return rutaRepository.findByActivaTrue();
    }

    public Optional<Ruta> buscarPorId(Long id) {
        return rutaRepository.findById(id);
    }

    public List<Ruta> buscarPorZona(String zona) {
        return rutaRepository.findByZona(zona);
    }

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        if (ruta == null) throw new IllegalArgumentException("Ruta nula");
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta actualizarRuta(Ruta ruta) {
        if (ruta == null) throw new IllegalArgumentException("Ruta nula");
        if (!rutaRepository.existsById(ruta.getId())) {
            throw new IllegalStateException("La ruta no existe");
        }
        return rutaRepository.save(ruta);
    }

    @Transactional
    public void eliminarRuta(Long id) {
        if (!rutaRepository.existsById(id)) {
            throw new IllegalArgumentException("Ruta no encontrada con ID: " + id);
        }
        rutaRepository.deleteById(id);
    }
}