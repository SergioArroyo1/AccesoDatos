DROP DATABASE IF EXISTS RA3;
CREATE DATABASE IF NOT EXISTS RA3;
USE RA3;

CREATE TABLE usuario(
                        id int auto_increment primary key not null,
                        username varchar(100) unique not null,
                        contrasena varchar(255) not null,
                        activo tinyint(1) default 1
);




Select * from usuario;


