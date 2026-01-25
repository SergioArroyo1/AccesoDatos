package com.example.Ra3.DTO;
import lombok.*;
import java.time.LocalDateTime;
/**
 * DTO enriquecido para vistas (admin/user), incluye datos del departamento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioViewDTO {
    /** ID del usuario */
    private Long id;
    /** Username */
    private String username;
    /** Email */
    private String email;
    /** Estado activo/inactivo */
    private boolean estado;
    /** Rol del usuario (admin/user) */
    private String rol;
    /** Fecha de creación */
    private LocalDateTime fechaCreacion;

    // Campos extra para departamento
    /** ID del departamento (puede ser null) */
    private Long departamentoId;
    /** Nombre del departamento (puede ser null) */
    private String departamentoNombre;
}
