package conexion;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Clase de utilidad para obtener y cerrar una única instancia de {@link Connection}.
 */
public class ConexionBD {
    // Conexión compartida por la clase; se inicializa perezosamente en getConexion().
    private static Connection conexion = null;

    /**
     * Devuelve la conexión a la base de datos.
     * 1. Si la conexión es nula o está cerrada, lee las propiedades desde
     * el recurso \`db.properties\` del classpath y crea una nueva Connection
     * mediante DriverManager.getConnection(...).

     * 2. Si la conexión ya existe y está abierta, simplemente la devuelve.
     */
    public static Connection getConexion() throws Exception {
        // Comprueba si la conexión necesita ser creada o reestablecida.
        if (conexion == null || conexion.isClosed()) {
            Properties props = new Properties();

            /*
             * Lectura del fichero de configuración desde el classpath.
             * try-with-resources garantiza el cierre del InputStream aunque se produzca
             * una excepción durante la carga de propiedades.
             */
            try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
                props.load(input);
            }

            // Obtención de la Connection usando los valores leídos del fichero de propiedades.
            conexion = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));
            System.out.println("Conexión establecida con la BD");
        }
        return conexion;
    }

    /**
     * Cierra la conexión si está abierta.
     */
    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) conexion.close();
        } catch (SQLException e) {
            // En este ejemplo docente imprimimos la traza; en producción usar un logger.
            e.printStackTrace();
        }
    }
}
