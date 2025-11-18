package org.example;

import conexion.Consultas;

import java.util.Scanner;

public class Menu {
    Scanner sc = new Scanner(System.in);
    Consultas consultas = new Consultas();

    public void MostrarMenu(){
        int opcion;
        do {
            System.out.println("1. Ingresar Empleados");
            System.out.println("2. Mostrar Empleados");
            System.out.println("3. Actualizar Empleados");
            System.out.println("4. Eliminar Empleados");
            System.out.println("5. Procedimiento Almacenado");
            System.out.println("6. Transferir presupuesto a salario");
            System.out.println("7 . Salir");
            System.out.println("Introduce una opcion: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    consultas.InsertarEmpleados();
                    break;
                case 2:
                    consultas.MostrarEmpleados();
                    break;
                case 3:
                    consultas.ActualizarEmpleados();
                    break;
                case 4:
                    consultas.EliminarEmpleado();
                    break;
                case 5:
                    consultas.ProcedimientoAlmacenado();
                    break;
                case 6:
                    consultas.TransferirPresupuesto();
                    break;
                case 7:
                    System.out.println("Saliendo...");
                default:
                    System.out.println("Opcion incorrecta.");
            }
        }while (opcion != 0);

        sc.close();
    }
}

