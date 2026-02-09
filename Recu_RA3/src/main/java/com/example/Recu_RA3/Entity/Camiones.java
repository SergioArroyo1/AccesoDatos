package com.example.Recu_RA3.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camiones")
public class Camiones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "matricula", nullable = false, length = 10, unique = true)
    private String matricula;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "capacidad_kg", nullable = false)
    private double capacidad_kg;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCamion estado = EstadoCamion.DISPONIBLE;

    @Column(name = "fecha_alta", nullable = false)
    private Date fechaAlta;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "camion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // evita recursión hacia atrás
    private List<Asignacion> asignaciones = new ArrayList<>();
}