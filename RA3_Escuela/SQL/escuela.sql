-- 1. CREACIÓN DE LA ESTRUCTURA
DROP DATABASE IF EXISTS centro_educativo;
CREATE DATABASE IF NOT EXISTS centro_educativo;
USE centro_educativo;

-- Tabla de Usuarios
CREATE TABLE usuario (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         nombre VARCHAR(100) NOT NULL,
                         activo BOOLEAN DEFAULT TRUE,
                         fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Alumnos
CREATE TABLE alumno (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellidos VARCHAR(100) NOT NULL,
                        dni VARCHAR(15) NOT NULL UNIQUE,
                        curso VARCHAR(50),
                        fecha_nacimiento DATE,
                        observaciones TEXT,
                        profesor_id INT,
                        activo BOOLEAN DEFAULT TRUE,
                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_profesor FOREIGN KEY (profesor_id) REFERENCES usuario(id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE
);

-- Tabla de Roles
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nombre_rol VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla Intermedia Roles-Usuario
CREATE TABLE roles_usuario (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               id_usuario INT,
                               id_rol INT,
                               FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
                               FOREIGN KEY (id_rol) REFERENCES roles(id) ON DELETE CASCADE
);



-- 3. INSERCIÓN DE USUARIOS (Password para todos: 1234)
-- Hash BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2Lvs8LmuLYC4W76tW9.q8mG0yv6i
INSERT INTO usuario (username, email, password_hash, nombre) VALUES
                                                                 ('admin', 'admin@escuela.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'Administrador General'),
                                                                 ('profesor1', 'profe1@escuela.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'Juan Profesor'),
                                                                 ('profesor2', 'profe2@escuela.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'Maria Profesora'),
                                                                 ('recepcion1', 'recepcion@escuela.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'Lucia Recepción');

INSERT INTO alumno (nombre, apellidos, dni, curso, fecha_nacimiento, observaciones, profesor_id) VALUES
                                                                                                     ('Carlos', 'García', '12345678A', '1º ESO', '2010-05-15', 'Alumno muy participativo, destaca en matemáticas.', 2),
                                                                                                     ('Elena', 'Pérez', '87654321B', '1º ESO', '2010-08-20', 'Requiere apoyo extra en lengua extranjera.', 2),
                                                                                                     ('Marcos', 'López', '11223344C', '2º ESO', '2009-03-10', 'Interés alto en actividades deportivas y extraescolares.', 2),
                                                                                                     ('Sofia', 'Ruiz', '44332211D', '2º ESO', '2009-11-05', 'Excelente conducta, delegada de clase.', 3),
                                                                                                     ('David', 'Sanz', '55667788E', '3º ESO', '2008-01-25', 'Buen rendimiento general, falta mejorar puntualidad.', 3),
                                                                                                     ('Laura', 'Mora', '99887766F', '3º ESO', '2008-07-12', 'Gran capacidad analítica, mención honorífica en ciencias.', 3);
-- 2. INSERCIÓN DE DATOS MAESTROS (ROLES)
INSERT INTO roles (nombre_rol) VALUES ('admin'), ('profesor'), ('recepcion');


-- 4. ASIGNACIÓN DE ROLES
INSERT INTO roles_usuario (id_usuario, id_rol) VALUES
                                                   (1, 1),
                                                   (2, 2),
                                                   (3, 2),
                                                   (4, 3);


-- 2. Crear tabla principal de sesiones
CREATE TABLE SPRING_SESSION (
                                PRIMARY_ID CHAR(36) NOT NULL,
                                SESSION_ID CHAR(36) NOT NULL,
                                CREATION_TIME BIGINT NOT NULL,
                                LAST_ACCESS_TIME BIGINT NOT NULL,
                                MAX_INACTIVE_INTERVAL INT NOT NULL,
                                EXPIRY_TIME BIGINT NOT NULL,
                                PRINCIPAL_NAME VARCHAR(100),
                                CONSTRAINT PK_SPRING_SESSION PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

-- 3. Crear índices para rendimiento
CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

-- 4. Crear tabla de atributos (donde se guardan los datos de sesión)
CREATE TABLE SPRING_SESSION_ATTRIBUTES (
                                           SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                                           ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                                           ATTRIBUTE_BYTES BLOB NOT NULL,
                                           CONSTRAINT PK_SPRING_SESSION_ATTRIBUTES PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                                           CONSTRAINT FK_SPRING_SESSION_ATTRIBUTES_SPRING_SESSION FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION (PRIMARY_ID) ON DELETE CASCADE
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

SELECT * FROM alumno;
SELECT * FROM usuario;