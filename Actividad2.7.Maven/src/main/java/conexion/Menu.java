package conexion;


import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Consultas consultas = new Consultas();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Insertar empleado");
            System.out.println("2. Insertar proyecto");
            System.out.println("3. Procedimiento Almacenado");
            System.out.println("4. Realizar transaccion");
            System.out.println("5. Salir");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    consultas.InsertarEmpleados();
                    break;
                case "2":
                    consultas.InsertarProyectos();
                    break;
                case "3":
                    consultas.ProcedimientoAlmacenado();
                    break;
                case "4":
                    consultas.RealizarTransaccion();
                    break;
                case "5":
                    System.out.println("Saliendo... ");
                    return;
                default:
                    System.out.println("Opción no válida");
                    sc.close();

            }
        }
    }
}