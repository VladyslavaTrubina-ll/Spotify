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
('Isabel LaRosa', 'Pop', 'Cantante estadounidense'), -- ID 3
('Krymov', 'Electronic', 'Productor musical'),      -- ID 4
('Joe Rogan', 'Podcast', 'Podcaster famoso'),       -- ID 5
('Imagine Dragons', 'Rock', 'Banda de Las Vegas');  -- ID 6 (NUEVO GRUPO)

INSERT INTO musico (idMusico, caracteristica) VALUES 
(1, 'Solista'), 
(2, 'Solista'), 
(3, 'Solista'), 
(4, 'Solista'), 
(6, 'Grupo'); -- Asociamos ID 6 como Grupo

INSERT INTO podcaster (idPodcaster) VALUES (5);

-- 3. AUDIO (Añadimos la canción del grupo)
INSERT INTO audio (nombre, duracion, archivo, tipo) VALUES 
('Talk', '00:03:00', 'talk.mp3', 'Cancion'),           -- ID 1
('Rozmova z mistom', '00:03:20', 'rozmova.mp3', 'Cancion'), -- ID 2
('Older', '00:02:45', 'older.mp3', 'Cancion'),         -- ID 3
('You', '00:03:15', 'you.mp3', 'Cancion'),             -- ID 4
('JRE #2000', '02:30:00', 'jre2000.mp3', 'Podcast'),   -- ID 5
('Believer', '00:03:24', 'believer.mp3', 'Cancion');   -- ID 6 (NUEVA CANCIÓN)

-- 4. ALBUM (Añadimos álbum del grupo)
INSERT INTO album (titulo, ano, genero, idMusico) VALUES 
('Culturally Appropriate', '2022-11-25', 'Electronic', 1),
('Evolve', '2017-06-23', 'Rock', 6); -- Álbum del grupo Imagine Dragons (ID 6)

-- 5. CANCION y PODCAST (Relacionamos los audios con sus tablas hijas)
INSERT INTO cancion (idCancion, idAlbum, artistasInvitados) VALUES 
(1, 1, NULL), 
(2, NULL, NULL), 
(3, NULL, NULL), 
(4, NULL, NULL),
(6, 2, 'Lil Wayne'); -- Canción "Believer" en Álbum "Evolve" con invitado

INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES (5, 1, 5);

-- 6. CLIENTE y PREMIUM
INSERT INTO cliente (nombre, apellidos, usuario, contrasena, fechaNacimiento, idIdioma) VALUES 
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
(6, 1, CURDATE()), -- Believer en la playlist 1
(6, 2, CURDATE()); -- Believer en la playlist 2

INSERT INTO favoritos (idCliente, idAudio) VALUES 
(1, 3), -- Juan le dio like a Older
(2, 6); -- Elena le dio like a Believer

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
