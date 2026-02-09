
DROP DATABASE IF EXISTS gestion_residuos;
CREATE DATABASE gestion_residuos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_residuos;


CREATE TABLE usuario (

                         id int  AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         nombre VARCHAR(100) NOT NULL,
                         activo BOOLEAN DEFAULT TRUE,
                         fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP

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


CREATE TABLE camiones (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          id_usuario int,
                          matricula VARCHAR(10) NOT NULL UNIQUE,
                          modelo VARCHAR(100) NOT NULL,
                          capacidad_kg DECIMAL(10,2) NOT NULL,
                          estado ENUM('DISPONIBLE', 'EN_RUTA', 'MANTENIMIENTO') NOT NULL DEFAULT 'DISPONIBLE',
                          fecha_alta DATE NOT NULL,
                          activo BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) ENGINE=InnoDB;


CREATE TABLE rutas (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       id_usuario int,
                       nombre VARCHAR(100) NOT NULL,
                       zona VARCHAR(100) NOT NULL,
                       dia_semana ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES') NOT NULL,
                       hora_inicio TIME NOT NULL,
                       hora_fin TIME NOT NULL,
                       activa BOOLEAN DEFAULT TRUE,
                       FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) ENGINE=InnoDB;


CREATE TABLE asignaciones (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              camion_id BIGINT NOT NULL,
                              ruta_id BIGINT NOT NULL,
                              fecha_asignacion DATE NOT NULL DEFAULT (CURRENT_DATE),
                              CONSTRAINT fk_asignacion_camion FOREIGN KEY (camion_id) REFERENCES camiones(id) ON DELETE CASCADE,
                              CONSTRAINT fk_asignacion_ruta FOREIGN KEY (ruta_id) REFERENCES rutas(id) ON DELETE CASCADE,
                              CONSTRAINT uk_camion_ruta UNIQUE (camion_id, ruta_id)
) ENGINE=InnoDB;

INSERT INTO usuario (username, email, password_hash, nombre) VALUES
                                                                 ('admin',       'admin@residuos.es',       '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'Ana Martínez García'),
                                                                 ('coord.garcia', 'garcia@residuos.es',     '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'Carlos García López'),
                                                                 ('coord.lopez',  'lopez@residuos.es',      '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'María López Fernández');
insert into roles( nombre_rol) values ('admin');
insert into roles( nombre_rol) values ('coordinador');

insert into roles_usuarios(id_usuario, id_rol) values(1,1);
insert into roles_usuarios(id_usuario,id_rol) values(2,2);
insert into roles_usuarios(id_usuario, id_rol) values(3,2);

INSERT INTO camiones (id_usuario,matricula, modelo, capacidad_kg, estado, fecha_alta) VALUES
                                                                                          (2,'1234-ABC', 'Iveco Daily 70C',     7000.00,  'DISPONIBLE',    '2023-03-15'),
                                                                                          (2,'5678-DEF', 'Mercedes Econic',     12000.00, 'EN_RUTA',       '2022-07-20'),
                                                                                          (2,'9012-GHI', 'Renault D-Wide',      10000.00, 'DISPONIBLE',    '2024-01-10'),
                                                                                          (3,'3456-JKL', 'MAN TGS 26.320',     15000.00, 'MANTENIMIENTO', '2021-11-05'),
                                                                                          (3,'7890-MNO', 'Volvo FE Electric',   8000.00,  'EN_RUTA',       '2024-06-01');

-- Rutas de recogida
INSERT INTO rutas (id_usuario, nombre, zona, dia_semana, hora_inicio, hora_fin) VALUES
                                                                                    (2,'Ruta Centro Mañana',    'Centro',       'LUNES',     '06:00', '12:00'),
                                                                                    (2,'Ruta Norte Tarde',      'Norte',        'MARTES',    '14:00', '20:00'),
                                                                                    (3,'Ruta Sur Mañana',       'Sur',          'MIERCOLES', '06:00', '12:00'),
                                                                                    (3,'Ruta Polígono Industrial', 'Polígono',  'JUEVES',    '07:00', '15:00');

-- Asignaciones (camión ↔ ruta)
INSERT INTO asignaciones (camion_id, ruta_id, fecha_asignacion) VALUES
                                                                    (1, 1, '2025-01-15'),   -- Iveco Daily → Ruta Centro Mañana
                                                                    (2, 1, '2025-01-15'),   -- Mercedes Econic → Ruta Centro Mañana
                                                                    (2, 3, '2025-01-16'),   -- Mercedes Econic → Ruta Sur Mañana
                                                                    (3, 2, '2025-01-15'),   -- Renault D-Wide → Ruta Norte Tarde
                                                                    (5, 4, '2025-01-17'),   -- Volvo FE Electric → Ruta Polígono Industrial
                                                                    (1, 4, '2025-01-17');   -- Iveco Daily → Ruta Polígono Industrial


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
