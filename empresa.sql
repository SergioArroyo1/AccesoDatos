CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

DROP DATABASE empresa;


CREATE TABLE empleados (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(50),
salario DOUBLE
);

INSERT INTO empleados (nombre, salario)
VALUES ('Ana', 25000), ('Luis', 28000), ('Marta', 32000);

DELIMITER //
CREATE PROCEDURE obtener_empleado(IN empId INT)
BEGIN
    SELECT id, nombre FROM empleados WHERE id = empId;
END //
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE incrementar_salario(
    IN p_id INT,               -- id del empleado
    IN p_incremento DOUBLE,    -- cantidad a sumar al salario
    OUT p_nuevo_salario DOUBLE -- salario actualizado
)
BEGIN
    -- Actualizamos el salario del empleado
    UPDATE empleados
    SET salario = salario + p_incremento
    WHERE id = p_id;

    -- Devolvemos el nuevo salario actualizado
    SELECT salario INTO p_nuevo_salario
    FROM empleados
    WHERE id = p_id;
END$$

DELIMITER ;


CREATE TABLE cuentas (
id INT AUTO_INCREMENT PRIMARY KEY,
titular VARCHAR(100),
saldo DECIMAL(10,2)
);
INSERT INTO cuentas (titular, saldo) VALUES
('Ana', 2000.00),
('Luis', 1500.00);

CREATE TABLE logs (
paso VARCHAR (100),
descripcion VARCHAR (100),
fecha datetime
);

Select * from cuentas;

Select * from logs;


Select * from logs;
