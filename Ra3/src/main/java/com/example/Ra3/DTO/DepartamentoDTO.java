package com.example.Ra3.DTO;

import lombok.*;

/**
 * DTO (Data Transfer Object) para representar un Departamento
 * - Se usa en las respuestas/solicitudes del controlador de departamentos
 * - Evita exponer directamente la entidad JPA
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoDTO {
    /** Identificador único del departamento (clave primaria) */
    private Long id;
    /** Nombre único del departamento */
    private String nombre;
}
