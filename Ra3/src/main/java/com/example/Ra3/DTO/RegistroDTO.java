package com.example.Ra3.DTO;

/**
 * DTO para registrar un nuevo usuario.
 * Se usa en el endpoint POST /registro.
 */
public class RegistroDTO {
    private String username;
    private String email;
    private String password;

    public RegistroDTO() {}

    public RegistroDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
