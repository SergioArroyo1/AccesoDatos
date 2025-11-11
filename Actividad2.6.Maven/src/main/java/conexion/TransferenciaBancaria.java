package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.util.Scanner;

public class TransferenciaBancaria {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String user = "root";
        String password = "root123";

        Scanner sc = new Scanner(System.in);

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            conexion.setAutoCommit(false);
            Savepoint afterRetiro = null;

            try {
                // Paso 1: retirar de la cuenta 1
                System.out.println("Ingrese el monto a retirar de la cuenta 1:");
                double montoRetiro = sc.nextDouble();

                String retirarSQL = "UPDATE cuentas SET saldo = saldo - ? WHERE id = 1";
                try (PreparedStatement pstmtRet = conexion.prepareStatement(retirarSQL)) {
                    pstmtRet.setDouble(1, montoRetiro);
                    int filas = pstmtRet.executeUpdate();
                    if (filas != 1) {
                        throw new SQLException("No se pudo retirar: filas afectadas = " + filas);
                    }
                }

                // Registrar log del primer paso
                String insertLogSQL = "INSERT INTO logs (paso, descripcion, fecha) VALUES (?, ?, NOW())";
                try (PreparedStatement pstmtLog1 = conexion.prepareStatement(insertLogSQL)) {
                    pstmtLog1.setString(1, "RETIRO");
                    pstmtLog1.setString(2, "Retiro en cuenta 1 por monto: " + montoRetiro);
                    int filasLog = pstmtLog1.executeUpdate();
                    if (filasLog != 1) {
                        throw new SQLException("No se pudo insertar log del retiro");
                    }
                }

                // Creamos el savepoint para poder mantener el primer paso si falla el segundo
                afterRetiro = conexion.setSavepoint("after_retiro");

                // Paso 2: depositar en la cuenta 2
                System.out.println("Ingrese el monto a depositar en la cuenta 2:");
                double montoDeposito = sc.nextDouble();

                String depositarSQL = "UPDATE cuentas SET saldo = saldo + ? WHERE id = 2";
                try (PreparedStatement pstmtDep = conexion.prepareStatement(depositarSQL)) {
                    pstmtDep.setDouble(1, montoDeposito);
                    int filasDep = pstmtDep.executeUpdate();
                    if (filasDep != 1) {
                        throw new SQLException("No se pudo depositar: filas afectadas = " + filasDep);
                    }
                }

                // Registrar el log del segundo paso
                try (PreparedStatement pstmtLog2 = conexion.prepareStatement(insertLogSQL)) {
                    pstmtLog2.setString(1, "DEPOSITO");
                    pstmtLog2.setString(2, "Deposito en cuenta 2 por monto: " + montoDeposito);
                    int filasLog2 = pstmtLog2.executeUpdate();
                    if (filasLog2 != 1) {
                        throw new SQLException("No se pudo insertar log del deposito");
                    }
                }

                // Si llegamos aquí, ambos pasos y sus logs se realizaron correctamente
                conexion.commit();
                System.out.println("Transferencia completada con éxito.");

            } catch (Exception e) {
                // Si hubo un error en el segundo paso o su log, se revierte hasta el savepoint (se mantiene el primer paso)
                try {
                    if (afterRetiro != null) {
                        // Revertir al savepoint: se mantiene todo lo anterior al savepoint (el retiro y su log)
                        conexion.rollback(afterRetiro);
                        // Confirmar el commit de lo que quedó antes del savepoint
                        conexion.commit();
                        System.out.println("Error en el segundo paso o en su registro. Se revirtió hasta el savepoint y se mantuvo el primer paso.");
                    } else {
                        // No había savepoint (falló el primer paso o su log), revertir todo
                        conexion.rollback();
                        System.out.println("Error en la transferencia. Se ha revertido toda la transacción.");
                    }
                } catch (SQLException ex) {
                    // Si rollback falla, intenta el rollback total
                    try {
                        conexion.rollback();
                        System.out.println("Error al intentar rollback parcial; se revertió toda la transacción.");
                    } catch (SQLException ex2) {
                        System.err.println("Fallo al revertir la transacción: ");
                        ex2.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }

    }
}