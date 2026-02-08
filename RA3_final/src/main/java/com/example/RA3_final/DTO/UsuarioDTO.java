package com.example.RA3_final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String username;
    private String email;
    private String nombre;
    //private String password;
    private String rol;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public UsuarioDTO(int id, String username, String email, String nombre, String rol, boolean activo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
    }
}
