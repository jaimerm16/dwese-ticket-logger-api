DROP TABLE IF EXISTS regions;
DROP TABLE IF EXISTS provincias;

-- then recreate tables

-- Crear tabla para las Comunidades Autónomas de España
CREATE TABLE IF NOT EXISTS regions (
 id INT AUTO_INCREMENT PRIMARY KEY,
 code VARCHAR(10) NOT NULL UNIQUE,
 name VARCHAR(100) NOT NULL,
 image VARCHAR(100)
);

-- Crear tabla para las Provincias de España
CREATE TABLE IF NOT EXISTS provincias (
 id INT AUTO_INCREMENT PRIMARY KEY,
 code VARCHAR(10) NOT NULL UNIQUE,
 name VARCHAR(100) NOT NULL
);

-- Crear la tabla 'tickets'
CREATE TABLE IF NOT EXISTS tickets (
 id INT AUTO_INCREMENT PRIMARY KEY,
 date DATETIME NOT NULL,
 discount DECIMAL(5, 2) NOT NULL
);

-- Crear la tabla 'products'
CREATE TABLE IF NOT EXISTS products (
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(255) NOT NULL,
 price DECIMAL(10, 2) NOT NULL
);

-- Crear la tabla 'product_ticket'
CREATE TABLE IF NOT EXISTS product_ticket (
 product_id INT NOT NULL,
 ticket_id INT NOT NULL,
 PRIMARY KEY (product_id, ticket_id),
 FOREIGN KEY (product_id) REFERENCES products(id),
 FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

ALTER TABLE regions
ADD COLUMN IF NOT EXISTS provincia_id INT,
ADD CONSTRAINT fk_region_provincia
    FOREIGN KEY (provincia_id)
    REFERENCES provincias(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Crear la tabla 'users'
CREATE TABLE IF NOT EXISTS users (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 username VARCHAR(50) UNIQUE NOT NULL,
 password VARCHAR(100) NOT NULL,
 enabled BOOLEAN NOT NULL,
 first_name VARCHAR(50) NOT NULL,
 last_name VARCHAR(50) NOT NULL,
 image VARCHAR(255),
 created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 last_password_change_date TIMESTAMP
);

-- Crear la tabla 'roles'
CREATE TABLE IF NOT EXISTS roles (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(50) UNIQUE NOT NULL
);

-- Crear la tabla 'user_roles'
CREATE TABLE IF NOT EXISTS user_roles (
 user_id BIGINT NOT NULL,
 role_id BIGINT NOT NULL,
 PRIMARY KEY (user_id, role_id),
 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
 FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);