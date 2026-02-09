package com.example.Recu_RA3.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AJUSTA el nombre de columna al que existe en tu BD: camion_id (no id_camion)
    @ManyToOne
    @JoinColumn(name = "camion_id", nullable = false)
    @JsonIgnoreProperties({"usuario", "asignaciones"})
    private Camiones camion;

    // AJUSTA el nombre de columna al que existe en tu BD: ruta_id (si aplica)
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    @JsonIgnoreProperties({"usuario", "asignaciones"})
    private Ruta ruta;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;
}