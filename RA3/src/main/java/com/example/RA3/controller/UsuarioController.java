package com.example.RA3.controller;


import com.example.RA3.model.Usuario;
import com.example.RA3.repository.UsuarioRepository;
import com.example.RA3.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


@Component
public class UsuarioController implements CommandLineRunner {
    public static Scanner scanner = new Scanner(System.in);

        @Autowired
        UsuarioService usuarioService;

        @Autowired
        UsuarioRepository usuarioRepository;

        @Override
        public void run(String... args) throws Exception {
            if (iniciarSesion()) {
                System.out.println("Sesion Iniciada");

                while (true) {

                    System.out.println("\n--- MENU USUARIO ---");
                    System.out.println("1. Test de conexión");
                    System.out.println("2. Crear usuario");
                    System.out.println("3. Listar usuarios ");
                    System.out.println("4. Buscar por username");
                    System.out.println("5. Actualizar Usuario");
                    System.out.println("6. Desactivar usuario (borrado lógico)");
                    System.out.println("7. Eliminar usuario (borrado físico)");
                    System.out.println("0. Salir");
                    System.out.print("Elige una opción: ");

                    String opcion = scanner.nextLine();

                    switch (opcion) {
                        case "1":

                            break;
                        case "2":
                            darDeAltaUsuario();
                            break;
                        case "3":
                            mostrarUsuarios();
                            break;
                        case "4":
                            mostrarUsuarioPorUser();
                            break;
                        case "5":
                            actualizarUsuario();
                            break;
                        case "6":
                            desactivarUsuario();
                            break;
                        case "7":
                            eliminarUsuario();
                            break;
                        case "0":
                            System.out.println("Vuelve pronto.");
                            System.exit(0);
                        default:
                            System.out.println("Opción no válida.");
                    }
                }
            }
        }

        public boolean iniciarSesion(){
            boolean logeado = false;
            while(!logeado){
                System.out.println("Dame un username");
                String username = scanner.nextLine();

                try{
                    if (!usuarioService.comprobarUsername(username)) throw new IllegalStateException("El usuario no existe.");
                    System.out.println("Dime la contraseña: ");
                    String contrasena = scanner.nextLine();

                    if(usuarioService.comprobarInicioSesion(new Usuario(username, contrasena))){
                        logeado = true;
                    }

                }catch (IllegalArgumentException | IllegalStateException e){
                    System.out.println(e.getMessage());
                }
            }
            return logeado;

        }

        public void darDeAltaUsuario(){
            try{
                System.out.println("Dime el username: ");
                String username = scanner.nextLine();
                if(usuarioService.comprobarUsername(username)) throw new IllegalStateException("El usuario ya existe");
                System.out.println("Dime la contraseña: ");
                String contrasena = scanner.nextLine();
                Usuario usuario = new Usuario(username, contrasena);
                Usuario usuarioCreado = usuarioService.CrearUsuario(usuario);

                if(usuarioCreado == null) throw new IllegalStateException("El usuario no se ha creado correctamente");
                System.out.println("El usuario se ha creado correctamente con el id " + usuarioCreado.getId());

            }catch (IllegalArgumentException | IllegalStateException e){
                System.out.println(e.getMessage());
            }
        }

        public void mostrarUsuarios(){
            try{
                System.out.println("LISTA DE USUARIOS");
                List<Usuario> lista = usuarioService.listaUsuarios();
                if (lista.isEmpty()) throw new IllegalStateException("No existe ningun usuario");

                for(Usuario u : lista){
                    System.out.println("ID: " + u.getId() + " USERNAME: " + u.getUsername() + " ESTADO: " + (u.isActivo()?"ACTIVO" : "INACTIVO"));
                }

            }catch (IllegalArgumentException | IllegalStateException e){
                System.out.println(e.getMessage());
            }

        }

        public void mostrarUsuarioPorUser(){
            try{
                System.out.println("Introduce el username a buscar: ");
                String username = scanner.nextLine();

                Optional<Usuario> buscausuario = usuarioService.buscaUsuario(username);

                System.out.println("ID: " + buscausuario.get().getId() + " USERNAME: " + buscausuario.get().getUsername() + " ESTADO: " + (buscausuario.get().isActivo()?"ACTIVO" : "INACTIVO"));

            }catch (IllegalArgumentException | IllegalStateException e){
                System.out.println(e.getMessage());
            }
        }

        public void actualizarUsuario(){
            try{
                System.out.println("Introduce el username a actualizar: ");
                String username = scanner.nextLine();

                Optional<Usuario> updateUsuario = usuarioService.buscaUsuario(username);

                System.out.println("ID: " + updateUsuario.get().getId() + " USERNAME: "+ updateUsuario.get().getUsername() + " ESTADO: " + (updateUsuario.get().isActivo()?"ACTIVO" : "INACTIVO"));

                System.out.println("Introduce el nuevo username: ");
                String nuevousername = scanner.nextLine();

                if(!StringUtils.hasText(nuevousername)) throw new IllegalArgumentException("El nombre viene vacio.");
                updateUsuario.get().setUsername(nuevousername);
                Usuario usuarioActualizado = usuarioService.actualizaUsuario(updateUsuario.get());

                System.out.println("ID: " + usuarioActualizado.getId() + " USERNAME: "+ usuarioActualizado.getUsername() + " ESTADO: " + (usuarioActualizado.isActivo()?"ACTIVO" : "INACTIVO"));


            }catch (IllegalArgumentException | IllegalStateException e){
                System.out.println(e.getMessage());
            }
        }

        public void desactivarUsuario(){
            System.out.println("Primero introduce el username");
            String username = scanner.nextLine();
            try {
                if(!usuarioService.comprobarUsername(username)){
                    throw new IllegalStateException("El usuario no existe");
                }
                Usuario usuario = usuarioService.desactivoUsuario(username);
                if (usuario == null) {
                    throw new IllegalStateException("Problemas al cambiar el estado");
                }
                System.out.println("Estado cambiado a desactivado");
                String linea =  " | Nombre de usuario: " + usuario.getUsername() +  " | Estado: " + (usuario.isActivo() ? "Está activo" : "No está activo");
                System.out.println(linea);
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

    public void eliminarUsuario(){
        System.out.println("Primero introduce el nickname");
        String username = scanner.nextLine();
        try {
            if(!usuarioService.comprobarUsername(username)){
                throw new IllegalStateException("El usuario no existe");
            }
            if (usuarioService.eliminaUsuario(username)) {
                System.out.println("Usuario Eliminado correctamente");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }


}
