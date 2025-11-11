package conexion;

import java.sql.*;


import static conexion.ConexionPool.getConexion;

public class PruebaPool {
    public static void main(String[] args) {
        try {
        Connection c1 = getConexion();
        Connection c2 = getConexion();
        Connection c3 = getConexion();

        if (c1 != null) {
            System.out.println("Conexión 1 obtenida del pool.");
            Statement stmt = c1.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM empleados");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }

            }

        if (c2 != null) {
            System.out.println("Conexión 2 obtenida del pool.");
            Statement stmt = c2.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM empleados");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }
        }

        if (c3 != null) {
            System.out.println("Conexión 3 obtenida del pool.");
            Statement stmt = c3.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM empleados");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }
        }

        } catch (SQLException e) {
            System.err.println("Error al obtener conexión del pool: " + e.getMessage());

        } finally {
            // Cerrar el pool al finalizar la aplicación
            ConexionPool.cerrarPool();
        }
    }
}