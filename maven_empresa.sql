CREATE DATABASE maven_empresa;
USE maven_empresa;
DROP DATABASE maven_empresa;

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2) NOT NULL
);

CREATE TABLE proyectos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presupuesto DECIMAL(10,2) NOT NULL
);

CREATE TABLE asignaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT,
    id_proyecto INT,
    fecha_asignacion DATE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id),
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id)
);

DELIMITER //

CREATE PROCEDURE asignar_empleado_proyecto (
    IN p_id_empleado INT,
    IN p_id_proyecto INT
)
BEGIN
    INSERT INTO asignaciones (id_empleado, id_proyecto, fecha_asignacion)
    VALUES (p_id_empleado, p_id_proyecto, CURDATE());
END //

DELIMITER ;

SELECT e.id AS id_empleado, e.nombre AS empleado, p.id AS id_proyecto, p.nombre AS proyecto
FROM empleados e
CROSS JOIN proyectos p;

Select * from empleados;

Select * from proyectos;
