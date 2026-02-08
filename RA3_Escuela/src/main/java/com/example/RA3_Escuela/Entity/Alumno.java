package com.example.RA3_Escuela.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Table(name = "alumno")
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "dni", nullable = false, unique = true, length = 15)
    private String dni;

    @Column(name="curso", length = 50)
    private String curso;

    @Column(name ="fecha_nacimiento", updatable = false)
    private Date fecha_nacimiento;

    @Column(name= "observaciones", columnDefinition = "Text")
    private String observaciones;

    @Column(name ="activo", nullable = false )
    private boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Usuario usuario;
}
