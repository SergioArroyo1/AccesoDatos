package com.example.Ra3.Modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA que representa la tabla 'departamento'.
 * - Relación uno-a-muchos con Usuario: un departamento tiene muchos usuarios.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departamento")
public class Departamento {

    /** Clave primaria autoincremental */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Long id;

    /** Nombre único del departamento */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Relación inversa: lista de usuarios que pertenecen a este departamento.
     * mappedBy indica que la FK está en Usuario (campo 'departamento').
     */
    @OneToMany(mappedBy = "departamento")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Usuario> usuarios = new HashSet<>();
}