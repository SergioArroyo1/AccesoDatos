package com.example.RA3_final.Service;

import com.example.RA3_final.Entity.Roles;
import com.example.RA3_final.Repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    RolesRepository rolesRepository;

    public Optional<Roles> buscarPorNombre(String rolNombre) {
        return rolesRepository.findByNombreRol(rolNombre);
    }
}
