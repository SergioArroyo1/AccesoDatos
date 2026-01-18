package com.example.RA3.model;


import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "Usuario")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username",unique = true, length = 100, nullable = false)
    private String username;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "activo")
    private boolean activo = true;

    public Usuario(String username, String contrasena) {
        this.username = username;
        this.contrasena = contrasena;
    }
}