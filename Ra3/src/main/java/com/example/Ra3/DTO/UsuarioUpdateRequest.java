package com.example.Ra3.DTO;
import lombok.*;

/**
 * Payload para actualizar usuarios desde el panel admin.
 * Incluye cambios en username, email, rol, estado y departamento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateRequest {
    /** Nuevo username (opcional) */
    private String username;
    /** Nuevo email (opcional) */
    private String email;
    /** Nuevo rol (admin/user) */
    private String rol;     // admin/user
    /** Nuevo estado (true=activo, false=inactivo) */
    private boolean estado;
    /** ID del departamento asignado (puede ser null para "sin departamento") */
    private Long departamentoId; // NUEVO
}
