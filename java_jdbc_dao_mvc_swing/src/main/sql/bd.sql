/* Garante que o banco de teste exista */
CREATE DATABASE IF NOT EXISTS java_mvc_test_db;

/* Seleciona o banco de teste para os comandos seguintes */
USE java_mvc_test_db;

/* Apaga a tabela 'users' se ela já existir, para começar do zero */
DROP TABLE IF EXISTS users;

/* Cria a tabela 'users' novamente */
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    login varchar(30) not null unique key,
    name varchar(30) not null,
    PRIMARY KEY (id)
) ENGINE=INNODB;