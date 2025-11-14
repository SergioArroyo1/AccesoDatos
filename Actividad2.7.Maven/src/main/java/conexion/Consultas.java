package conexion;

import java.sql.*;
import java.util.Calendar;
import java.util.Scanner;

public class Consultas {
    private final Scanner scanner = new Scanner(System.in);
    // URL de conexión JDBC a la base de datos MySQL (host, puerto y nombre de la base de datos)
    private final String url = "jdbc:mysql://127.0.0.1:3306/maven_empresa";
    // Usuario de la base de datos
    private final String user = "root";
    // Contraseña de la base de datos
    private final String password = "root123";

    // Insertar empleados asumiendo que 'id' es AUTO_INCREMENT en la tabla
    public void InsertarEmpleados() {
        String sql = "INSERT INTO empleados (nombre, salario) VALUES (?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            System.out.print("Introduce el nombre del empleado: ");
            String nombreEmpleado = scanner.nextLine();
            System.out.print("Introduce el salario: ");
            double salario = Double.parseDouble(scanner.nextLine());

            pstmt.setString(1, nombreEmpleado);
            pstmt.setDouble(2, salario);

            int filasInsertadas = pstmt.executeUpdate();
            System.out.println("Filas insertadas: " + filasInsertadas);

        } catch (SQLException e) {
            System.err.println("Error SQL en InsertarEmpleados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Insertar proyectos: ajustado para columnas (nombre, presupuesto) — cambia según tu esquema
    public void InsertarProyectos() {
        String sql = "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            System.out.print("Introduce el nombre del proyecto: ");
            String nombreProyecto = scanner.nextLine();

            System.out.print("Introduce el presupuesto: ");
            double presupuesto = Double.parseDouble(scanner.nextLine());

            ps.setString(1, nombreProyecto);
            ps.setDouble(2, presupuesto);

            int filasInsertadas = ps.executeUpdate();
            System.out.println("Filas insertadas: " + filasInsertadas);
        } catch (SQLException e) {
            System.err.println("Error SQL en InsertarProyectos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ProcedimientoAlmacenado() {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            try (CallableStatement cs = conexion.prepareCall("{call asignar_empleado_proyecto(?,?)}")) {
                System.out.print("Ingrese el id del empleado: ");
                int idEmpleado = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Ingrese el id del proyecto: ");
                int idProyecto = scanner.nextInt();
                scanner.nextLine();

                cs.setInt(1, idEmpleado);
                cs.setInt(2, idProyecto);

                boolean hasResultSet = cs.execute();

                if (!hasResultSet) {
                    int updateCount = cs.getUpdateCount();
                    System.out.println("Procedimiento ejecutado. Filas afectadas: " + updateCount);
                } else {
                    System.out.println("Procedimiento ejecutado y devolvió un resultado.");
                }
            }
        } catch (SQLException e) {
            // Mostrar información detallada del error para depuración
            System.err.println("Error al realizar el procedimiento: " + e.getMessage());
            e.printStackTrace();

        }
    }

    public void RealizarTransaccion(){
        try(Connection conexion = DriverManager.getConnection(url, user, password)){
            conexion.setAutoCommit(false);

            try {
                System.out.println("Introduce el id del empleado");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Introduce el salario a aumentar del empleado: ");
                double ingresoSalario = scanner.nextDouble();
                scanner.nextLine();

                String ingresarSQL = "Update empleados SET salario = salario + ? where id = ?";
                try(PreparedStatement pstmIngreso = conexion.prepareStatement(ingresarSQL)){
                    pstmIngreso.setDouble(1, ingresoSalario);
                    pstmIngreso.setInt(2,id);
                    int filasIngreso = pstmIngreso.executeUpdate();
                    if (filasIngreso != 1){
                        throw new SQLException("No se pudo ingresar: filas afectadas = " + filasIngreso);
                    }
                }

                System.out.println("Introduce el id del proyecto: ");
                int idPro = scanner.nextInt();
                scanner.nextLine();
                String DescontarSQL = "Update proyectos SET presupuesto = presupuesto - ? where id = ?";
                try(PreparedStatement pstmDescuento = conexion.prepareStatement(DescontarSQL)){
                    pstmDescuento.setDouble(1, ingresoSalario);
                    pstmDescuento.setInt(2, idPro);

                    int filasDeposito = pstmDescuento.executeUpdate();
                    if(filasDeposito != 1){
                        throw new SQLException("No se puedo descontar: filas afectadas = " + filasDeposito);
                    }
                }

                conexion.commit();
                System.out.println("Transaccion realizada");


            } catch (Exception e) {
                conexion.rollback();
                System.out.println("Error en la transacción: " + e.getMessage());
            } finally {
                conexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println(" Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

    }


}