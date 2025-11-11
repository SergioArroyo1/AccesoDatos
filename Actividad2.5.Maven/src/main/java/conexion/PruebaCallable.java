package conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Scanner;

public class PruebaCallable {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String url = "jdbc:mysql://127.0.0.1:3306/empresa";
        String user = "root";
        String password = "1234";

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {

            // Pedimos al usuario que introduzca el ID del empleado
            System.out.print("Introduce el ID del empleado: ");
            int idEmpleado = sc.nextInt();

            // Pedimos al usuario que introduzca el incremento salarial
            System.out.print("Introduce el incremento salarial: ");
            double incremento = sc.nextDouble();

            CallableStatement cs = conexion.prepareCall("{CALL incrementar_salario(?, ?, ?)}");

            // Asignamos valores a los parámetros de entrada
            cs.setInt(1, idEmpleado);
            cs.setDouble(2, incremento);

            // Registramos el parámetro de salida
            cs.registerOutParameter(3, Types.DOUBLE);

            // Ejecutamos el procedimiento
            cs.execute();

            // Obtenemos el nuevo salario
            double nuevoSalario = cs.getDouble(3);

            // Mostramos el resultado
            System.out.println("El nuevo salario del empleado es: " + nuevoSalario + " €");

        } catch (Exception e) {
            System.err.println("Error al ejecutar el procedimiento: " + e.getMessage());
        } finally {
            sc.close(); // Cerramos el Scanner
        }
    }
}

