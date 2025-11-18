package conexion;

import com.mysql.cj.xdevapi.StreamingSqlResultBuilder;

import javax.swing.text.html.HTMLDocument;
import java.sql.*;
import java.util.Scanner;

public class Consultas {

    Scanner sc = new Scanner(System.in);

    public void InsertarEmpleados() {
        String sqlInsertar = "INSERT INTO empleados (nombre, salario) VALUES (?, ?)";
        try (Connection con = ConexionPool.getConexion()) {
            PreparedStatement ps = con.prepareStatement(sqlInsertar);
            {

                System.out.println("Ingrese el nombre del empleado: ");
                String nombre = sc.nextLine();

                System.out.println("Ingrese el salario: ");
                double salario = sc.nextDouble();
                sc.nextLine();

                ps.setString(1, nombre);
                ps.setDouble(2, salario);

                int filas = ps.executeUpdate();

                System.out.println("Filas insertadas: " + filas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void MostrarEmpleados() {
        String sqlMostrar = "SELECT * FROM empleados";
        try (Connection con = ConexionPool.getConexion()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sqlMostrar);

            System.out.println("\nLOS EMPLEADOS");

            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ActualizarEmpleados() {
        String sqlUpdate = "UPDATE empleados SET nombre = ?, salario = ? WHERE id = ?";
        try (Connection con = ConexionPool.getConexion()) {
            PreparedStatement pst = con.prepareStatement(sqlUpdate);

            System.out.println("Introduce el id del empleado para actualizar:");
            int idEmpleado = sc.nextInt();
            sc.nextLine();

            System.out.println("Introduce el nuevo nombre: ");
            String nombre = sc.nextLine();

            System.out.println("Introduce el nuevo salario:");

            double salario = sc.nextDouble();
            sc.nextLine();

            pst.setString(1, nombre);
            pst.setDouble(2, salario);
            pst.setInt(3, idEmpleado);

            pst.executeUpdate();

            System.out.println("Empleado actualizado.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void EliminarEmpleado() {
        String sqlEliminar = "DELETE FROM empleados WHERE id = ?";
        try (Connection con = ConexionPool.getConexion()) {
            PreparedStatement pst = con.prepareStatement(sqlEliminar);

            System.out.println("Ingrese el id del empleado a eliminar: ");
            int idEliminar = sc.nextInt();
            sc.nextLine();

            pst.setInt(1, idEliminar);

            pst.executeUpdate();

            System.out.println("Empleado eliminado con exito.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ProcedimientoAlmacenado() {

        System.out.println("Introduce el id del empleado:");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("Introduce el id del proyecto a asignar: ");
        int idProyecto = sc.nextInt();
        sc.nextLine();

        String sqlProcedimiento = "{CALL asignar_empleado_proyecto (?, ?)}";
        try (Connection con = ConexionPool.getConexion()) {
            CallableStatement cs = con.prepareCall(sqlProcedimiento);
            {

                cs.setInt(1, id);
                cs.setInt(2, idProyecto);

                cs.executeUpdate();

                System.out.println("Empleado asignado a proyecto");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void TransferirPresupuesto() {
        Connection conex = null;
        try {
            conex = ConexionPool.getConexion();
            conex.setAutoCommit(false);

            String sqlRestar = "UPDATE proyectos SET presupuesto = presupuesto - ? WHERE id = ?";
            try (PreparedStatement psRestar = conex.prepareStatement(sqlRestar)) {

                System.out.println("Introduce el id del proyecto: ");
                int idPro = sc.nextInt();
                sc.nextLine();
                System.out.println("Introduce el dinero que quieres descontar del presupuesto: ");
                double dinero = sc.nextDouble();
                sc.nextLine();
                psRestar.setDouble(1, dinero);
                psRestar.setInt(2, idPro);
                psRestar.executeUpdate();
            }

            String sqlSumar = "UPDATE empleados SET salario = salario + ? WHERE id = ?";

            try (PreparedStatement psSumar = conex.prepareStatement(sqlSumar)) {

                System.out.println("Introduce el id del empleado: ");
                int idEmp = sc.nextInt();
                sc.nextLine();

                System.out.println("Introduce la cantidad a aumentar: ");
                double cantidad = sc.nextDouble();
                sc.nextLine();
                psSumar.setDouble(1, cantidad);
                psSumar.setInt(2, idEmp);
                psSumar.executeUpdate();

                System.out.println("Salario aumnetado con exito.");
            }

            conex.commit();

        } catch (SQLException e) {
            // Si hay error hacemos rollback para no dejar datos inconsistentes
            if (conex != null) {
                try {
                    conex.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Cerramos la conexión (si no se usa pool con cierre automático)
            if (conex != null) {
                try {
                    conex.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
