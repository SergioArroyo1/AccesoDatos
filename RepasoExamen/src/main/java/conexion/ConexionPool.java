package conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionPool {

    // Objeto del pool de conexiones (HikariDataSource)
    private static HikariDataSource dataSource;

    // Bloque estático: se ejecuta solo una vez cuando la clase se carga
    static {
        try {
            Properties props = new Properties();
            try (InputStream input = ConexionPool.class.getClassLoader().getResourceAsStream("db.properties")) {
                // Carga de las propiedades desde el fichero de configuración.
                // Se espera que 'db.url', 'db.user' y 'db.password' estén presentes.
                props.load(input);
            } catch (Exception ex) {
                throw new RuntimeException();
            }

            // Configuración mínima de HikariCP. Se explican los parámetros relevantes.
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));           // URL JDBC, p.ej. jdbc:mysql://host:3306/schema
            config.setUsername(props.getProperty("db.user"));         // Usuario de la BD
            config.setPassword(props.getProperty("db.password"));     // Contraseña de la BD
            config.setMaximumPoolSize(5);    // Máximo de 5 conexiones activas
            config.setMinimumIdle(2);        // Al menos 2 conexiones en espera
            config.setIdleTimeout(30000);    // Tiempo de inactividad (30s)
            config.setMaxLifetime(1800000);  // Vida máxima de una conexión (30 minutos)
            // Creamos el pool
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConexion(){
        try{
            return dataSource.getConnection();


        }catch (SQLException e){
            throw  new RuntimeException();
        }
    }
    /**
     * cerrar el pool al finalizar la aplicación
     */
    public static void cerrarPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado correctamente.");
        }
    }
}