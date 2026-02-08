package com.example.RA3_final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDTO implements Serializable {
    private int id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String telefono;
    private Date fechaNacimiento;
    private String historial;
    private boolean activo;

    public PacienteDTO(String nombre, String apellidos, String dni, String telefono, Date fechaNacimiento, String historial, boolean activo) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.historial = historial;
        this.activo = activo;
    }
}
