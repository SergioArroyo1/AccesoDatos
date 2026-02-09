package com.example.Recu_RA3.DTO;

import com.example.Recu_RA3.Entity.EstadoCamion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamionesDTO implements Serializable {
    private int id;
    private String matricula;
    private String modelo;
    private double capacidadKg;
    private EstadoCamion estado; // ← CAMBIADO de String a EstadoCamion
    private Date fechaAlta;
    private boolean activo;

    public CamionesDTO(String matricula, String modelo, double capacidadKg, Date fechaAlta, boolean activo) {
        this.matricula = matricula;
        this.modelo = modelo;
        this.capacidadKg = capacidadKg;
        this.fechaAlta = fechaAlta;
        this.activo = activo;
    }
}