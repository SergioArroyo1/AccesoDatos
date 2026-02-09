package com.example.Recu_RA3.Service;


import com.example.Recu_RA3.Entity.Roles;
import com.example.Recu_RA3.Repository.RolesRepository;
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
