package conexion;

public class Main {
    public static void main(String[] args) {
        Consultas consultas = new Consultas();
        consultas.mostrarEmpleados();
        consultas.ResultadoMetaData();
        consultas.DatabaseMetaData();
        }
    }
