package conexion;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionPool {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();

            // try-with-resources para cerrar automáticamente el InputStream
            // Se utiliza ConexionBD.class.getClassLoader() para obtener el classloader.
            try (InputStream input = ConexionPool.class.getClassLoader().getResourceAsStream("db.properties")) {
                // Carga de las propiedades desde el fichero de configuración.
                // Se espera que 'db.url', 'db.user' y 'db.password' estén presentes.
                props.load(input);
            }

            // Configuración mínima de HikariCP. Se explican los parámetros relevantes.
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));

            // Parámetros de tuning básicos (ejemplo didáctico)
            config.setMaximumPoolSize(5);     // Número máximo de conexiones activas en el pool
            config.setMinimumIdle(2);        // Número mínimo de conexiones inactivas a mantener
            config.setIdleTimeout(30000);    // Tiempo (ms) tras el cual una conexión inactiva puede cerrarse

            // Creación del DataSource (pool) a partir de la configuración anterior.
            dataSource = new HikariDataSource(config);
            System.out.println("Pool de conexiones HikariCP inicializado correctamente.");
        } catch (Exception e) {
            e.getMessage();
        }
    }
    // metodo para coger la conexion
    public static Connection getConexion() throws SQLException {
        return dataSource.getConnection();
    }
    // metodo para cerrar la conexion
    public static void cerrarPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado.");
        }
    }
}