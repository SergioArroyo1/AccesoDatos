package com.example.Ra3.Service;

import com.example.Ra3.Modelo.Departamento;
import com.example.Ra3.Repository.DepartamentoRepository;
import com.example.Ra3.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
/**
 * Servicio de departamentos:
 * - Lógica de validación y negocio para CRUD de departamentos.
 */
@Service
public class DepartamentoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Devuelve todos los departamentos */
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    /** Busca un departamento por ID */
    public Optional<Departamento> buscarPorId(Long id) {
        return departamentoRepository.findById(id);
    }

    /** Crea un departamento nuevo validando nombre y unicidad */
    public Departamento crear(String nombre) {
        if (!StringUtils.hasText(nombre)) throw new IllegalArgumentException("Nombre vacío");
        if (departamentoRepository.existsByNombre(nombre.trim()))
            throw new IllegalStateException("Nombre ya existe");
        Departamento d = new Departamento();
        d.setNombre(nombre.trim());
        return departamentoRepository.save(d);
    }

    /** Actualiza el nombre del departamento (valida cambios y unicidad) */
    public Departamento actualizar(Long id, String nombre) {
        Departamento d = departamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("No existe"));
        if (!StringUtils.hasText(nombre)) throw new IllegalArgumentException("Nombre vacío");
        if (!d.getNombre().equalsIgnoreCase(nombre.trim()) &&
                departamentoRepository.existsByNombre(nombre.trim())) {
            throw new IllegalStateException("Nombre ya existe");
        }
        d.setNombre(nombre.trim());
        return departamentoRepository.save(d);
    }

    /**
     * Elimina un departamento si no está en uso por usuarios.
     * Lanza DataIntegrityViolationException si hay usuarios asignados.
     */
    public void eliminar(Long id) {
        long usados = usuarioRepository.countByDepartamento_Id(id);
        if (usados > 0) throw new DataIntegrityViolationException("Departamento en uso");
        departamentoRepository.deleteById(id);
    }
}