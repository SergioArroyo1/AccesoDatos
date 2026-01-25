package com.example.Ra3.Service;

import com.example.Ra3.Modelo.Rol;
import com.example.Ra3.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Servicio de roles (admin/user).
 * Encapsula el acceso al repositorio y posibles validaciones.
 */
@Service
public class RolService {
    @Autowired
    RolRepository rolRepository;

    /** Devuelve la lista completa de roles */
    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    /** Busca un rol por su nombre (admin/user) */
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}


