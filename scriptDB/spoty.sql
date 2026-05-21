DROP DATABASE IF EXISTS spoty;
CREATE DATABASE spoty CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spoty;

-- 1. IDIOMA
CREATE TABLE IDIOMA (
    idIdioma INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL
);

-- 2. ARTISTA
CREATE TABLE ARTISTA (
    idArtista INT AUTO_INCREMENT PRIMARY KEY,
    nombreArtistico VARCHAR(50) UNIQUE NOT NULL,
    imagen VARCHAR(255),
    genero VARCHAR(50),
    descripcion VARCHAR(200)
);

-- 3. MUSICO (Relación 'es' en image_1f692a.png)
CREATE TABLE MUSICO (
    idMusico INT PRIMARY KEY,
    caracteristica ENUM('Solista', 'Grupo') NOT NULL,
    CONSTRAINT fk_musico_artista FOREIGN KEY (idMusico) REFERENCES ARTISTA(idArtista) ON DELETE CASCADE
);

-- 4. PODCASTER (Relación 'es')
CREATE TABLE PODCASTER (
    idPodcaster INT PRIMARY KEY,
    CONSTRAINT fk_podcaster_artista FOREIGN KEY (idPodcaster) REFERENCES ARTISTA(idArtista) ON DELETE CASCADE
);

-- 5. AUDIO (Entidad principal para Canción/Podcast)
CREATE TABLE AUDIO (
    idAudio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    duracion TIME NOT NULL,
    archivo VARCHAR(255) NOT NULL,
    tipo ENUM('Cancion', 'Podcast') NOT NULL,
    nReproducciones INT DEFAULT 0
);

-- 6. ALBUM
CREATE TABLE ALBUM (
    idAlbum INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(50) NOT NULL,
    ano YEAR,
    genero VARCHAR(50),
    imagen VARCHAR(255),
    idMusico INT,
    CONSTRAINT fk_album_musico FOREIGN KEY (idMusico) REFERENCES MUSICO(idMusico) ON DELETE SET NULL
);

-- 7. CANCION (Especialización de AUDIO)
CREATE TABLE CANCION (
    idCancion INT PRIMARY KEY,
    idAlbum INT,
    artistasInvitados VARCHAR(255),
    CONSTRAINT fk_cancion_audio FOREIGN KEY (idCancion) REFERENCES AUDIO(idAudio) ON DELETE CASCADE,
    CONSTRAINT fk_cancion_album FOREIGN KEY (idAlbum) REFERENCES ALBUM(idAlbum) ON DELETE CASCADE
);

-- 8. PODCAST (Especialización de AUDIO)
CREATE TABLE PODCAST (
    idPodcast INT PRIMARY KEY,
    colaboradores VARCHAR(255), -- Cambiado a VARCHAR según lógica habitual
    idPodcaster INT,
    CONSTRAINT fk_podcast_audio FOREIGN KEY (idPodcast) REFERENCES AUDIO(idAudio) ON DELETE CASCADE,
    CONSTRAINT fk_podcast_podcaster FOREIGN KEY (idPodcaster) REFERENCES PODCASTER(idPodcaster) ON DELETE CASCADE
);

-- 9. CLIENTE
CREATE TABLE CLIENTE (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(80),
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    fechaNacimiento DATE,
    fechaRegistro DATE DEFAULT (CURRENT_DATE),
    tipo ENUM('Free', 'Premium') NOT NULL DEFAULT 'Free',
    idIdioma INT,
    CONSTRAINT fk_cliente_idioma FOREIGN KEY (idIdioma) REFERENCES IDIOMA(idIdioma)
);

-- 10. PREMIUM
CREATE TABLE PREMIUM (
    idCliente INT PRIMARY KEY,
    fechaCaducidad DATE NOT NULL,
    CONSTRAINT fk_premium_cliente FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente) ON DELETE CASCADE
);

-- 11. PLAYLIST
CREATE TABLE PLAYLIST (
    idPlaylist INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(80) NOT NULL,
    fechaCreacion DATE NOT NULL,
    idCliente INT,
    CONSTRAINT fk_playlist_cliente FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente) ON DELETE CASCADE
);

-- 12. PLAYLIST_CANCIONES (Corregido según image_1e9a5f.png)
CREATE TABLE PLAYLIST_CANCIONES (
    idCancion INT, -- Cambiado de idAudio a idCancion para cumplir con el diagrama
    idPlaylist INT,
    fechaPlayList_Cancion DATE DEFAULT (CURRENT_DATE),
    PRIMARY KEY (idCancion, idPlaylist),
    CONSTRAINT fk_pc_cancion FOREIGN KEY (idCancion) REFERENCES CANCION(idCancion) ON DELETE CASCADE,
    CONSTRAINT fk_pc_playlist FOREIGN KEY (idPlaylist) REFERENCES PLAYLIST(idPlaylist) ON DELETE CASCADE
);

-- 13. FAVORITOS (Relación 'gustar' N:M)
CREATE TABLE FAVORITOS (
    idCliente INT,
    idAudio INT,
    PRIMARY KEY (idCliente, idAudio),
    CONSTRAINT fk_fav_cliente FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente) ON DELETE CASCADE,
    CONSTRAINT fk_fav_audio FOREIGN KEY (idAudio) REFERENCES AUDIO(idAudio) ON DELETE CASCADE
);




-- 1. IDIOMA
INSERT INTO idioma (descripcion) VALUES ('Español'), ('English'), ('Ruso');

-- 2. ARTISTA y subtablas
INSERT INTO artista (nombreArtistico, genero, descripcion) VALUES 
('Salvatore Ganacci', 'Electronic', 'DJ sueco-bosnio'), -- ID 1
('Nikow', 'Hip-Hop', 'Artista polaco'),             -- ID 2
('Joe Rogan', 'Podcast', 'Podcaster famoso'),       -- ID 3
('Imagine Dragons', 'Rock', 'Banda de Las Vegas');  -- ID 4 (NUEVO GRUPO)

INSERT INTO musico (idMusico, caracteristica) VALUES 
(1, 'Solista'), 
(2, 'Solista'), 
(4, 'Grupo'); -- Asociamos ID 4 como Grupo

INSERT INTO podcaster (idPodcaster) VALUES (3);

-- 3. AUDIO (Añadimos la canción del grupo)
INSERT INTO audio (nombre, duracion, archivo, tipo) VALUES 
('Talk', '00:03:00', 'talk.mp3', 'Cancion'),           -- ID 1
('Rozmova z mistom', '00:03:20', 'rozmova.mp3', 'Cancion'), -- ID 2
('JRE #2000', '02:30:00', 'jre2000.mp3', 'Podcast'),   -- ID 3
('Believer', '00:03:24', 'believer.mp3', 'Cancion');   -- ID 4

-- 4. ALBUM (Añadimos álbum del grupo)
INSERT INTO album (titulo, ano, genero, idMusico) VALUES 
('Culturally Appropriate', 2022, 'Electronic', 1),
('Evolve', 2017, 'Rock', 4); -- Álbum del grupo Imagine Dragons (ID 4)

-- 5. CANCION y PODCAST (Relacionamos los audios con sus tablas hijas)
INSERT INTO cancion (idCancion, idAlbum, artistasInvitados) VALUES 
(1, 1, NULL), 
(2, NULL, NULL), 
(4, 2, 'Lil Wayne'); -- Canción "Believer" en Álbum "Evolve" con invitado

INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES (3, 1, 3);

-- 6. CLIENTE y PREMIUM
INSERT INTO cliente (nombre, apellidos, usuario, contrasena, fechaNacimiento, idIdioma) VALUES 
('Admin', 'Sistema', 'admin', 'admin', '2000-01-01', 1),
('Juan', 'Pérez', 'juanito88', 'hash_pass_123', '1995-05-10', 1),
('Elena', 'Gómez', 'elena_g', 'secure_pass_456', '2000-08-22', 2);

INSERT INTO premium (idCliente, fechaCaducidad) VALUES 
(1, '2026-12-31'); -- Juan es Premium, Elena es Free por defecto

-- 7. PLAYLIST y RELACIONES M:N (Añadimos la canción del grupo a la lista)
INSERT INTO playlist (titulo, fechaCreacion, IdCliente) VALUES 
('Favoritas 2026', CURDATE(), 1),
('Rock Essentials', CURDATE(), 2);

INSERT INTO playlist_canciones (idCancion, idPlaylist, fechaPlaylist_cancion) VALUES 
(1, 1, CURDATE()), -- Talk en la playlist 1
(4, 1, CURDATE()), -- Believer en la playlist 1
(4, 2, CURDATE()); -- Believer en la playlist 2

INSERT INTO favoritos (idCliente, idAudio) VALUES 
(2, 4); -- Elena le dio like a Believer

----------------------------------------------------------------------------------------------------
-- ===== VISTAS PARA ESTADÍSTICAS =====
DROP VIEW IF EXISTS cancionesmasescuchadas;
CREATE VIEW cancionesmasescuchadas AS
SELECT a.idAudio AS idCancion, a.nombre, a.nReproducciones
FROM audio a
JOIN cancion c ON a.idAudio = c.idCancion
ORDER BY a.nReproducciones DESC;

DROP VIEW IF EXISTS audiosmasescuchados;
CREATE VIEW audiosmasescuchados AS
SELECT a.idAudio, a.nombre, a.tipo, a.nReproducciones
FROM audio a
ORDER BY a.nReproducciones DESC;

DROP VIEW IF EXISTS podcastmasescuchado;
CREATE VIEW podcastmasescuchado AS
SELECT p.idPodcast, a.nombre, a.nReproducciones
FROM podcast p
JOIN audio a ON p.idPodcast = a.idAudio
ORDER BY a.nReproducciones DESC;

DROP VIEW IF EXISTS playlistmasescuchada;
CREATE VIEW playlistmasescuchada AS
SELECT pl.idPlaylist, pl.titulo, COUNT(pc.idCancion) AS totalCanciones
FROM playlist pl
LEFT JOIN playlist_canciones pc ON pl.idPlaylist = pc.idPlaylist
GROUP BY pl.idPlaylist, pl.titulo
ORDER BY totalCanciones DESC;

-- ==================================================================
-- ÍNDICES (uso recomendado: 3 índices para mejorar consultas frecuentes)
-- ==================================================================
CREATE INDEX idx_audio_nombre ON audio(nombre);
CREATE INDEX idx_artista_nombre ON artista(nombreArtistico);
CREATE INDEX idx_playlist_titulo ON playlist(titulo);

-- ==================================================================
-- VISTAS ADICIONALES (Resumen final)
-- ==================================================================
DROP VIEW IF EXISTS clientes_playlists_count;
CREATE VIEW clientes_playlists_count AS
SELECT c.idCliente, c.nombre, c.apellidos, COUNT(p.idPlaylist) AS numPlaylists
FROM cliente c
LEFT JOIN playlist p ON c.idCliente = p.idCliente
GROUP BY c.idCliente, c.nombre, c.apellidos;

-- ==================================================================
-- ROLES, USUARIOS Y PERMISOS
-- ==================================================================
-- Roles
DROP ROLE IF EXISTS role_read, role_write, role_admin;
CREATE ROLE role_read, role_write, role_admin;

-- Usuarios (ejemplo locales)
CREATE USER IF NOT EXISTS 'reader1'@'localhost' IDENTIFIED BY 'readerpass1';
CREATE USER IF NOT EXISTS 'reader2'@'localhost' IDENTIFIED BY 'readerpass2';
CREATE USER IF NOT EXISTS 'writer1'@'localhost' IDENTIFIED BY 'writerpass1';
CREATE USER IF NOT EXISTS 'writer2'@'localhost' IDENTIFIED BY 'writerpass2';
CREATE USER IF NOT EXISTS 'admin1'@'localhost' IDENTIFIED BY 'adminpass1';

-- Conceder privilegios a roles
GRANT SELECT ON spoty.* TO role_read;
GRANT SELECT, INSERT, UPDATE, DELETE ON spoty.* TO role_write;
GRANT ALL PRIVILEGES ON spoty.* TO role_admin;

-- Asignar roles a usuarios
GRANT role_read TO 'reader1'@'localhost', 'reader2'@'localhost';
GRANT role_write TO 'writer1'@'localhost', 'writer2'@'localhost';
GRANT role_admin TO 'admin1'@'localhost';

-- Hacer rol por defecto
SET DEFAULT ROLE role_read TO 'reader1'@'localhost';
SET DEFAULT ROLE role_write TO 'writer1'@'localhost';
SET DEFAULT ROLE role_admin TO 'admin1'@'localhost';

-- ==================================================================
-- PROCEDIMIENTOS Y FUNCIONES (3 ejemplos: crear_artista, actualizar_album, contar_playlists_cliente)
-- ==================================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS crear_artista$$
CREATE PROCEDURE crear_artista(
    IN p_nombre VARCHAR(100),
    IN p_genero VARCHAR(50),
    IN p_descripcion VARCHAR(200),
    IN p_imagen VARCHAR(255)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    SELECT COUNT(*) INTO v_count FROM artista WHERE nombreArtistico = p_nombre;
    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Artista ya existe';
    ELSE
        INSERT INTO artista (nombreArtistico, genero, descripcion, imagen) VALUES (p_nombre, p_genero, p_descripcion, p_imagen);
    END IF;
END$$

DROP PROCEDURE IF EXISTS actualizar_album$$
CREATE PROCEDURE actualizar_album(
    IN p_id INT,
    IN p_titulo VARCHAR(100),
    IN p_ano YEAR,
    IN p_genero VARCHAR(50),
    IN p_imagen VARCHAR(255),
    IN p_idMusico INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM album WHERE idAlbum = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Album no existe';
    ELSE
        UPDATE album SET titulo = p_titulo, ano = p_ano, genero = p_genero, imagen = p_imagen, idMusico = p_idMusico WHERE idAlbum = p_id;
    END IF;
END$$

DROP FUNCTION IF EXISTS contar_playlists_cliente$$
CREATE FUNCTION contar_playlists_cliente(p_idCliente INT) RETURNS INT DETERMINISTIC
BEGIN
    DECLARE v_count INT DEFAULT 0;
    SELECT COUNT(*) INTO v_count FROM playlist WHERE idCliente = p_idCliente;
    RETURN v_count;
END$$
DELIMITER ;

-- ==================================================================
-- EVENTO: limpieza mensual de playlists vacías (creadas hace más de 1 año)
-- ==================================================================
-- Nota: activar el event_scheduler global si está disponible: SET GLOBAL event_scheduler = ON;
DROP EVENT IF EXISTS evento_limpiar_playlists_vacias;
CREATE EVENT evento_limpiar_playlists_vacias
ON SCHEDULE EVERY 1 MONTH
DO
  DELETE FROM playlist WHERE idPlaylist IN (
    SELECT p.idPlaylist FROM (
      SELECT pl.idPlaylist FROM playlist pl LEFT JOIN playlist_canciones pc ON pl.idPlaylist = pc.idPlaylist WHERE pc.idPlaylist IS NULL AND pl.fechaCreacion < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
    ) AS p
  );

-- ==================================================================
-- CONSULTAS / PLANTILLAS SQL para acceso y mantenimiento desde Java
-- ==================================================================
-- Insertar artista (PreparedStatement):
-- INSERT INTO artista (nombreArtistico, genero, descripcion, imagen) VALUES (?, ?, ?, ?);

-- Actualizar álbum (PreparedStatement):
-- UPDATE album SET titulo = ?, ano = ?, genero = ?, imagen = ?, idMusico = ? WHERE idAlbum = ?;

-- Eliminar canción (PreparedStatement):
-- DELETE FROM cancion WHERE idCancion = ?;

-- Crear podcast y podcaster (ejemplo):
-- INSERT INTO artista (nombreArtistico, genero, descripcion) VALUES (?, 'Podcast', ?);
-- SET @idArt = LAST_INSERT_ID();
-- INSERT INTO podcaster (idPodcaster) VALUES (@idArt);
-- INSERT INTO audio (nombre, duracion, archivo, tipo) VALUES (?, ?, ?, 'Podcast');
-- INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES (LAST_INSERT_ID(), ?, @idArt);

-- Consultas útiles (SELECT) con JOINs:
-- Obtener canciones de un álbum:
-- SELECT a.idAudio, a.nombre, a.duracion FROM audio a JOIN cancion c ON a.idAudio = c.idCancion WHERE c.idAlbum = ?;

-- Uso de la función creada desde SQL (puede llamarse desde JDBC con SELECT contar_playlists_cliente(?)):
-- SELECT contar_playlists_cliente(?);

-- ==================================================================
-- DATOS ADICIONALES DE EJEMPLO (llenar tablas que falten o añadir más filas)
-- ==================================================================
INSERT INTO idioma (descripcion) VALUES ('Portugués');

INSERT INTO artista (nombreArtistico, genero, descripcion, imagen) VALUES
('The Testers', 'Indie', 'Banda de prueba', NULL),
('Coding Beats', 'Electronic', 'Proyecto de desarrolladores', NULL);

INSERT INTO musico (idMusico, caracteristica) VALUES
((SELECT idArtista FROM artista WHERE nombreArtistico = 'The Testers'), 'Grupo'),
((SELECT idArtista FROM artista WHERE nombreArtistico = 'Coding Beats'), 'Solista');

-- Añadir audios de ejemplo
INSERT INTO audio (nombre, duracion, archivo, tipo) VALUES
('Test Song', '00:02:30', 'test_song.mp3', 'Cancion'),
('Dev Podcast #1', '00:45:00', 'devpod1.mp3', 'Podcast');

-- Relacionar cancion y podcast con sus tablas hijas
INSERT INTO cancion (idCancion, idAlbum, artistasInvitados) VALUES
((SELECT idAudio FROM audio WHERE nombre = 'Test Song'), NULL, NULL);
INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES
((SELECT idAudio FROM audio WHERE nombre = 'Dev Podcast #1'), 'Equipo', (SELECT idPodcaster FROM podcaster LIMIT 1));

-- ==================================================================
-- INDICACIONES FINALES
-- - Ejecuta este script en un servidor MySQL/MariaDB con privilegios de administrador
-- - Para crear roles y usuarios necesitas privilegios de administración (CREATE USER, GRANT)
-- - Hice uso de SIGNAL/IF/variables en procedimientos para validar condiciones
-- - El EVENT usa una subconsulta con tabla derivada para evitar errores "You can't specify target table for update in FROM clause"
-- ==================================================================
