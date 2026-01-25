package com.example.Ra3.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


/**
 * Entidad JPA para la tabla 'usuario'.
 * - Contiene datos de autenticación y relación con rol/es y departamento.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="usuario")
public class Usuario {

    /** ID del usuario (auto-incremental) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_usuario")
    private Long id;

    /** Username único */
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    /** Email único */
    @Column(name= "email", nullable = false, unique = true, length = 100)
    private String email;

    /** Contraseña hasheada (columna 'password' en BD) */
    @Column(name = "password", nullable = false, length = 100)
    private String contra;

    /** Estado del usuario (activo/inactivo) */
    @Column(name="activo")
    private boolean activo = true;

    /** Fecha de creación (no se actualiza) */
    @Column(name = "fecha_creacion", updatable = false )
    private LocalDateTime fechaCreacion ;

    /** Fecha de última actualización (se actualiza en BD) */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion ;

    // NUEVO: relación con departamento (muchos usuarios -> un departamento)
    /** Departamento al que pertenece el usuario (puede ser null) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    /** Roles asignados (admin/user). Relación con tabla intermedia usuario_rol */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles;

    /** Constructor de conveniencia para crear usuarios con solo username+password */
    public Usuario(String username, String contra) {
        this.username = username;
        this.contra = contra;
    }
}
