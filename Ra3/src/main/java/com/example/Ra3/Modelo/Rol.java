package com.example.Ra3.Modelo;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA para la tabla 'rol'.
 * - Relación muchos-a-muchos con Usuario (tabla intermedia usuario_rol).
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="rol")
public class Rol {
    /** ID del rol (auto-incremental) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_rol")
    private int idRol;

    /** Nombre único del rol (admin, user) */
    @Column(name= "nombre", unique = true, length = 100)
    private String nombre;

    /** Usuarios que tienen este rol (relación inversa) */
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Usuario> usuarios  = new HashSet<>();
}
