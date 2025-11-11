package conexion;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class TestConexion {
    public static void main(String[] args) {
        // 1. Cargar configuración desde db.properties
        Properties props = new Properties();
        try (InputStream input = TestConexion.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo db.properties");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 2. Obtener datos de conexión
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");


        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conexión establecida con éxito a la base de datos.");

            DatabaseMetaData meta = con.getMetaData();
            System.out.println("🔹 Driver: " + meta.getDriverName());
            System.out.println("🔹 Versión del driver: " + meta.getDriverVersion());
            System.out.println("🔹 Base de datos: " + meta.getDatabaseProductName());
            System.out.println("🔹 Versión BD: " + meta.getDatabaseProductVersion());
            System.out.println("🔹 Usuario conectado: " + meta.getUserName());
            System.out.println("🔹 URL de conexión: " + meta.getURL());

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM empleados")) {

                System.out.println("\n=== EMPLEADOS ===");
                while (rs.next()) {
                    System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                            rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
                }
            }

            try (PreparedStatement pst = con.prepareStatement("SELECT * FROM empleados WHERE id = ?")) {
                pst.setInt(1, 2); // Buscar empleado con ID 2
                try (ResultSet prs = pst.executeQuery()) {
                    System.out.println("\n=== EMPLEADO CON ID 2 ===");
                    if (prs.next()) {
                        System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                                prs.getInt("id"), prs.getString("nombre"), prs.getDouble("salario"));
                    } else {
                        System.out.println("No se encontró empleado con id 2.");
                    }
                }
            }

            try (CallableStatement cs = con.prepareCall("{call obtener_empleado(?)}")) {
                cs.setInt(1, 3);
                try (ResultSet rs1 = cs.executeQuery()) {
                    System.out.println("\n=== EMPLEADO OBTENIDO POR PROCEDIMIENTO ALMACENADO CON ID 3 ===");
                    if (rs1.next()) {
                        System.out.printf("ID: %d | Nombre: %s",
                                rs1.getInt("id"), rs1.getString("nombre"));
                    } else {
                        System.out.println("No se encontró empleado con id 3.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

    }
}