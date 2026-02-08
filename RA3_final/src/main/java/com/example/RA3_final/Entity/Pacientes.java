package com.example.RA3_final.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pacientes")
public class Pacientes {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre",nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos",nullable = false, length = 100)
    private String apellidos;

    @Column(name = "dni",nullable = false, unique = true, length = 100)
    private String dni;

    @Column(name = "telefono", length = 100)
    private String telefono;

    @Column(name = "fecha_nacimiento", updatable = false)
    private Date fechaNacimiento;

    @Column(name = "historial")
    private String historial;

    @Column(name = "activo",nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Usuario usuario;



}
