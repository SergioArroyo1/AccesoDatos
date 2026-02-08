drop database if exists  centro_medico;
CREATE DATABASE centro_medico ;
USE centro_medico;

    -- Crear tabla usuarios
CREATE TABLE usuario (
    
                          id int  AUTO_INCREMENT PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          activo BOOLEAN DEFAULT TRUE,
                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

-- Crear tabla pacientes
CREATE TABLE pacientes (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           apellidos VARCHAR(100) NOT NULL,
                           dni VARCHAR(15) NOT NULL UNIQUE,
                           telefono VARCHAR(20),
                           fecha_nacimiento DATE,
                           historial TEXT,
                           medico_id INT,
                           activo BOOLEAN DEFAULT TRUE,
                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (medico_id) REFERENCES usuario(id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);

CREATE TABLE roles(
    id int primary key  auto_increment not null,
    nombre_rol varchar(100) unique  not null

);

create table roles_usuarios(
    id int primary key auto_increment not null,
    id_usuario int,
    id_rol int,
    foreign key (id_usuario) references usuario(id) ON DELETE CASCADE,
    foreign key (id_rol) references  roles(id) ON DELETE CASCADE
);
insert into usuario ( username, email, password_hash, nombre) values ('sergio', 'admin@gmail.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'admin');
insert into usuario ( username, email, password_hash, nombre) values ('pepe', 'medico1@gmail.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'medico1');
insert into usuario ( username, email, password_hash, nombre) values ('wir', 'medico2@gmail.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'medico2');
insert into usuario ( username, email, password_hash, nombre) values ('warkoz', 'recepcion@gmail.com', '$2a$12$CzdU7n41SWsBnlPVerrdu.rAHZGeeYxXqkZYyyraM0YxrT6ry4b.2', 'recepcion');


insert into pacientes (nombre, apellidos,dni,telefono,fecha_nacimiento,historial,medico_id) values ('paciente1', 'gomez pila', '49589535V', 654864533, '2000-06-06', 'Paciente muy malito 1', 2);
insert into pacientes (nombre, apellidos,dni,telefono,fecha_nacimiento,historial,medico_id) values ('paciente2', 'gomez pila', '49589536V', 654864534, '2000-06-06', 'Paciente muy malito 2', 2);
insert into pacientes (nombre, apellidos,dni,telefono,fecha_nacimiento,historial,medico_id) values ('paciente3', 'gomez pila', '49589537V', 654864535, '2000-06-06', 'Paciente muy malito 3', 3);
insert into pacientes (nombre, apellidos,dni,telefono,fecha_nacimiento,historial,medico_id) values ('paciente4', 'gomez pila', '49589538V', 654864536, '2000-06-06', 'Paciente muy malito 4', 3);



insert into roles( nombre_rol) values ('admin');
insert into roles( nombre_rol) values ('medico');
insert into roles( nombre_rol) values ('recepcion');
insert into roles_usuarios(id_usuario, id_rol) VALUES (1,1);
insert into roles_usuarios(id_usuario, id_rol) VALUES (2,2);
insert into roles_usuarios(id_usuario, id_rol) VALUES (3,2);
insert into roles_usuarios(id_usuario, id_rol) VALUES (4,3);

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


select * from pacientes;