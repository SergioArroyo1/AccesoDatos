create database if not exists escuela;
use escuela;

create table usuario(
                        id_usuario int primary key auto_increment not null,
                        username varchar(100) unique not null,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password varchar(255) not null,
                        activo tinyint(1) default 1,
                        fecha_creacion timestamp default current_timestamp,
                        fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
create table rol(
                    id_rol int primary key auto_increment not null,
                    nombre varchar(100) unique not null
);


create table usuario_rol(
                            id int primary key auto_increment not null,
                            id_usuario int,
                            id_rol int,
                            FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);
/* contrasena 123 */
INSERT INTO usuario (username, email, password, activo) VALUES
                                                            ('admin', 'admin@empresa.com',
                                                             '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.IZPhFD3F4Wvqay', TRUE),
                                                            ('sergio', 'usuario1@empresa.com',
                                                             '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.IZPhFD3F4Wvqay', TRUE),
                                                            ('antonio', 'antonio@empresa.com',
                                                             '$2a$12$6Qw9rY9q1x2p8y6o9y6y5uQ4w9xYy0F3zZ8sG8k1y6qX8yZ6WkZ4G', TRUE);

INSERT INTO rol(nombre) values ('admin');
INSERT INTO rol(nombre) values ('user');
insert into usuario_rol(id_usuario,id_rol) values (1,1);
insert into usuario_rol(id_usuario,id_rol) values (2,2);
insert into usuario_rol(id_usuario,id_rol) values (3,2);

DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES;
DROP TABLE IF EXISTS SPRING_SESSION;


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
select * from usuario;

UPDATE usuario
SET password = '$2a$10$Hh.2t1mLKZ3NaLRd/KgyfO4idpYhCTVidIcs2rNy.WGbRO0fdu9Py'
WHERE username = 'admin';
UPDATE usuario
SET password = '$2a$10$Hh.2t1mLKZ3NaLRd/KgyfO4idpYhCTVidIcs2rNy.WGbRO0fdu9Py'
WHERE username = 'sergio';


-- … resto de tu script …

-- NUEVA tabla de departamentos
CREATE TABLE IF NOT EXISTS departamento (
                                            id_departamento INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                            nombre VARCHAR(100) UNIQUE NOT NULL
    );



-- 2) Añadir columna y FK a usuario (solo ejecuta una vez)
ALTER TABLE usuario
    ADD COLUMN id_departamento INT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_departamento
        FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento)
            ON DELETE SET NULL
            ON UPDATE CASCADE;

-- 3) Datos ejemplo
INSERT INTO departamento (nombre) VALUES ('Ventas') ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
INSERT INTO departamento (nombre) VALUES ('Soporte') ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
INSERT INTO departamento (nombre) VALUES ('IT')     ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 4) Asignación de departamentos a usuarios
UPDATE usuario SET id_departamento = (SELECT id_departamento FROM departamento WHERE nombre='IT') WHERE username='admin';
UPDATE usuario SET id_departamento = (SELECT id_departamento FROM departamento WHERE nombre='Ventas') WHERE username='sergio';
UPDATE usuario SET id_departamento = (SELECT id_departamento FROM departamento WHERE nombre='Soporte') WHERE username='antonio';
select * from departamento;