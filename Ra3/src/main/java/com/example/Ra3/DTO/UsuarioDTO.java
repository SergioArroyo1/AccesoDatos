package com.example.Ra3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * DTO base para representar un usuario autenticado o listado básico.
 * Se guarda en sesión (por eso implementa Serializable).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID del usuario */
    private Long id;
    /** Username único */
    private String username;
    /** Email único */
    private String email;
    /** Estado (activo/inactivo) */
    private boolean estado;
    /** Rol principal del usuario (admin/user) */
    private String rol;
    /** Fecha de creación de la cuenta */
    private LocalDateTime fechaCreacion;

    /**
     * Constructor de conveniencia para respuestas mínimas.
     */
    public UsuarioDTO(Long id, String username, String email, LocalDateTime fechaCreacion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fechaCreacion = fechaCreacion;
    }
}