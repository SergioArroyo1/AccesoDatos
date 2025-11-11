package conexion;

import java.sql.*;

public class Consultas {
    // URL de conexión JDBC a la base de datos MySQL (host, puerto y nombre de la base de datos)
    public String url = "jdbc:mysql://127.0.0.1:3306/empresa";
    // Usuario de la base de datos
    public String user = "root";
    // Contraseña de la base de datos
    public String password = "1234";


    /**
     * Muestra por consola todos los registros de la tabla 'empleados'.
     * - Crea un Statement y ejecuta una consulta SELECT * FROM empleados.
     * - Recorre el ResultSet e imprime cada fila con formato.
     */
    public void mostrarEmpleados() {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // Crear un Statement para ejecutar la consulta
            Statement st = conexion.createStatement();
            // Ejecutar la consulta y obtener el ResultSet
            ResultSet rs = st.executeQuery("SELECT * FROM empleados");
            System.out.println("\n=== EMPLEADOS ===");
            // Iterar por cada fila del ResultSet
            while (rs.next()) {
                // Obtener valores por nombre de columna y formatearlos en la salida
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"),           // obtener entero de la columna 'id'
                        rs.getString("nombre"),    // obtener String de la columna 'nombre'
                        rs.getDouble("salario"));  // obtener double de la columna 'salario'
            }

        } catch (Exception e) {
            // En caso de error, imprimir la traza de la excepción
            e.printStackTrace();
        }


    }

    /**
     * Muestra metadata del ResultSet obtenido al consultar la tabla 'empleados'.
     * - Imprime el número de columnas.
     * - Para cada columna imprime su nombre y tipo según el ResultSetMetaData.
     */
    public void ResultadoMetaData() {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // Crear Statement y ejecutar la misma consulta para obtener un ResultSet
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM empleados");
            System.out.println("\n=== METADATA DE RESULTSET ===");
            // Obtener el número de columnas desde el metadata del ResultSet
            int numeroColumnas = rs.getMetaData().getColumnCount();
            System.out.println("Número de columnas: " + numeroColumnas);
            // Los índices de columnas en JDBC comienzan en 1, por eso i = 1
            for (int i = 1; i <= numeroColumnas; i++) {
                // Imprimir el índice, nombre y tipo de la columna
                System.out.printf("Columna %d: %s (%s)%n",
                        i,
                        rs.getMetaData().getColumnName(i),
                        rs.getMetaData().getColumnTypeName(i));
            }

        } catch (Exception e) {
            // Imprimir errores si los hay
            e.printStackTrace();
        }
    }

    /**
     * Muestra metadata de la propia base de datos (DatabaseMetaData).
     * - Información como nombre del driver, versión y nombre/versión del SGBD.
     */
    public void DatabaseMetaData() {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // Obtener el objeto DatabaseMetaData a partir de la conexión
            DatabaseMetaData dbmd = conexion.getMetaData();
            System.out.println("\n=== METADATA DE BASE DE DATOS ===");
            // Imprimir varios atributos útiles para diagnóstico
            System.out.println("Nombre del Driver: " + dbmd.getDriverName());
            System.out.println("Versión del Driver: " + dbmd.getDriverVersion());
            System.out.println("Nombre del SGBD: " + dbmd.getDatabaseProductName());
            System.out.println("Versión del SGBD: " + dbmd.getDatabaseProductVersion());

        } catch (Exception e) {
            // Manejo básico de excepciones: imprimir la traza
            e.printStackTrace();


        }
    }
}