-- Inserts de las Comunidades Autónomas, ignora si se produce un error en la insercción
INSERT IGNORE INTO regions (id, code, name) VALUES
(1, '01', 'ANDALUCÍA'),
(2, '02', 'ARAGÓN'),
(3, '03', 'ASTURIAS'),
(4, '04', 'BALEARES'),
(5, '05', 'CANARIAS'),
(6, '06', 'CANTABRIA'),
(7, '07', 'CASTILLA Y LEÓN'),
(8, '08', 'CASTILLA-LA MANCHA'),
(9, '09', 'CATALUÑA'),
(10, '10', 'COMUNIDAD VALENCIANA'),
(11, '11', 'EXTREMADURA'),
(12, '12', 'GALICIA'),
(13, '13', 'MADRID'),
(14, '14', 'MURCIA'),
(15, '15', 'NAVARRA'),
(16, '16', 'PAÍS VASCO'),
(17, '17', 'LA RIOJA'),
(18, '18', 'CEUTA Y MELILLA');

-- Inserts de las Comunidades Autónomas, ignora si se produce un error en la insercción
INSERT IGNORE INTO provincias (id, code, name) VALUES
(1, '01', 'SEVILLA'),
(2, '02', 'MADRID'),
(3, '03', 'ZARAGOZA'),
(4, '04', 'BARCELONA'),
(5, '05', 'VALENCIA'),
(6, '06', 'HUELVA'),
(7, '07', 'LEÓN'),
(8, '08', 'TOLEDO'),
(9, '09', 'GERONA'),
(10, '10', 'BADAJOZ'),
(11, '11', 'CADIZ'),
(12, '12', 'OURENSE'),
(13, '13', 'CANTABRIA'),
(14, '14', 'MURCIA'),
(15, '15', 'NAVARRA'),
(16, '16', 'BILBAO'),
(17, '17', 'LA RIOJA'),
(18, '18', 'ALMERIA');

-- Insertar datos de ejemplo para 'roles'
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MANAGER'),
(3, 'ROLE_USER');
-- Insertar datos de ejemplo para 'users'. La contraseña de cada usuario es password
INSERT IGNORE INTO users (id, username, password, enabled, first_name,
last_name, image, created_date, last_modified_date, last_password_change_date)
VALUES
(1, 'admin', '$2b$12$FVRijCavVZ7Qt15.CQssHe9m/6eLAdjAv0PiOKFIjMU161wApxzye',
true, 'Admin', 'User', '/images/admin.jpg', NOW(), NOW(), NOW()),
(2, 'manager', '$2b$12$FVRijCavVZ7Qt15.CQssHe9m/6eLAdjAv0PiOKFIjMU161wApxzye',
true, 'Manager', 'User', '/images/manager.jpg', NOW(), NOW(), NOW()),
(3, 'normal', '$2b$12$FVRijCavVZ7Qt15.CQssHe9m/6eLAdjAv0PiOKFIjMU161wApxzye',
true, 'Regular', 'User', '/images/user.jpg', NOW(), NOW(), NOW());
-- Asignar el rol de administrador al usuario con id 1
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1);
-- Asignar el rol de gestor al usuario con id 2
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(2, 2);
-- Asignar el rol de usuario normal al usuario con id 3
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(3, 3);