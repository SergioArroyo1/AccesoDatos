package com.example.RA3_final.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    @Column( name = "username",unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email",unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombre",nullable = false, length = 100)
    private String nombre;

    @Column(name = "activo",nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @ManyToMany
    @JoinTable(
            name = "roles_usuarios",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "usuario",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Pacientes> pacientes = new ArrayList<>();

    }



