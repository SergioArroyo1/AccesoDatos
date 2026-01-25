package com.example.Ra3.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de login.
 * Contiene las credenciales introducidas por el usuario en el formulario.
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginDTO {
    /** Username de la cuenta con la que se quiere iniciar sesión */
    private String username;
    /** Password en texto plano (el backend la compara con el hash guardado) */
    private String password;
}