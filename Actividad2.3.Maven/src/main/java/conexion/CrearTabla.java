package conexion;

import java.math.BigDecimal;
import java.sql.*;

public class CrearTabla {
public static void main(String[] args) {
    String url = "jdbc:mysql://localhost:3306/empresa";
    String user = "root";
    String password = "root123";


    try (Connection con = DriverManager.getConnection(url, user, password);
         Statement stmt = con.createStatement()) {

        String sql = "CREATE TABLE IF NOT EXISTS proyectos (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "nombre VARCHAR(100) NOT NULL," +
                "presupuesto DECIMAL(10, 2) NOT NULL" +
                ")";
        stmt.executeUpdate(sql);

            // INSERT
            PreparedStatement psInsert = con.prepareStatement(
                    "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)");
        psInsert.setString(1, "Proyecto A");
        psInsert.setBigDecimal(2, BigDecimal.valueOf(1900.00));
        psInsert.executeUpdate();

        psInsert.setString(1, "Proyecto B");
        psInsert.setBigDecimal(2, BigDecimal.valueOf(2500.50));
        psInsert.executeUpdate();

        psInsert.setString(1, "Proyecto C");
        psInsert.setBigDecimal(2, BigDecimal.valueOf(3000.75));
        psInsert.executeUpdate();

            // UPDATE
            PreparedStatement psUpdate = con.prepareStatement(
                    "UPDATE proyectos SET presupuesto = ? WHERE id = ?");
            psUpdate.setDouble(1, 2000.00);
            psUpdate.setInt(2, 1);
            psUpdate.executeUpdate();

            // DELETE
            PreparedStatement psDelete = con.prepareStatement(
                    "DELETE FROM proyectos WHERE id = ?");
            psDelete.setInt(1, 2);
            psDelete.executeUpdate();

            System.out.println("Operaciones DML ejecutadas correctamente.");

            Statement stmtSelect = con.createStatement();
            ResultSet rs = stmtSelect.executeQuery("SELECT * FROM proyectos");
            System.out.println("\n=== Proyectos===");
            while (rs.next()) {
            System.out.printf("id: %d | nombre: %s | presupuesto: %.2f €%n",
                    rs.getInt("id"), rs.getString("nombre"), rs.getBigDecimal("presupuesto"));

            }

        System.out.println("Tabla 'proyectos' creada correctamente.");



    } catch (Exception e) {

        e.printStackTrace();
    }
}

}

