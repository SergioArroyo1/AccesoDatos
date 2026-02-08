package com.example.RA3_Escuela.Service;

import com.example.RA3_Escuela.Entity.Roles;
import com.example.RA3_Escuela.Repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    RolesRepository rolesRepository;

    public Optional<Roles> bucarPorNombre(String rolNombre){
        return rolesRepository.findByNombreRol(rolNombre);
    }


}
