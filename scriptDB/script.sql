CREATE DATABASE IF NOT EXISTS spoty;
USE spoty;

create table artista (
idArtista int unsigned auto_increment primary key,
nombreArtistico varchar(50) unique not null,
imagen varchar(255),
genero varchar(50),
descripcion varchar(200) 
);

create table musico (
idMusico int unsigned primary key,
caracteristica enum('Solista', 'Grupo') not null,
constraint fk_musico foreign key (idMusico) references artista(idArtista) on delete cascade
);

create table podcaster (
idPodcaster int unsigned primary key,
foreign key (idPodcaster) references artista(idArtista) on delete cascade
);

create table audio (
idAudio int unsigned auto_increment primary key,
nombre varchar(100) not null,
duracion time not null,
archivo varchar(255) not null,
tipo enum('Cancion', 'Podcast') not null,
nReproducciones int unsigned default 0
);

create table podcast (
idPodcast int unsigned primary key,
colaboradores int unsigned,
idPodcaster int unsigned,
constraint fk_podcast_audio foreign key (idPodcast) references audio(idAudio) on delete cascade,
constraint fk_podcaster_autor foreign key (idPodcaster) references podcaster(idPodcaster) on delete cascade 
);

create table album (
idAlbum int unsigned auto_increment primary key,
titulo varchar(50) not null, 
ano date not null,
genero varchar(40) not null,
imagen varchar(255),


idMusico int unsigned not null,
constraint fk_idMusico foreign key (idMusico) references musico(idMusico) on delete cascade
);

create table cancion (
idCancion int unsigned primary key,
idAlbum int unsigned,
artistasInvitados varchar(100),
constraint fk_idCancion foreign key (idCancion) references audio(idAudio) on delete cascade,
constraint fk_idAlbum foreign key (idAlbum) references album(idAlbum) on delete cascade
);

create table idioma(
idIdioma int auto_increment primary key,
description varchar(40)
);

create table cliente(
idCliente int unsigned auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100) not null,
usuario varchar(50) unique not null,
contrasena varchar(100) not null,
fechaNacimiento date not null,
fechaRegistro date not null DEFAULT CURRENT_TIMESTAMP,
tipo enum('Free', 'Premium') DEFAULT 'Free',
idIdioma INT,
constraint fk_idioma foreign key (idIdioma) references idioma(idIdioma)
);

create table playlist(
IDlist int unsigned auto_increment primary key,
titulo varchar(40) not null,
fechaCreacion date not null,
IdCliente int unsigned,
foreign key (IdCliente) references cliente(idCliente) on delete cascade
);

create table playlist_canciones(
idCancion int unsigned,
idPlaylist int unsigned,

fechaPlaylist_cancion date,
foreign key (idCancion) references cancion(idCancion) on delete cascade,


foreign key (idPlaylist) references playlist(IDlist) on delete cascade,
constraint fk_playlist_canciones primary key (idCancion,idPlaylist)
);

create table premium(
idCliente int unsigned primary key ,
fechaCaducidad date not null,
foreign key (idCliente) references cliente(idCliente) on delete cascade
);

create table favoritos(
idCliente int unsigned,
idAudio int unsigned,
foreign key (idCliente) references cliente(idCliente) on delete cascade,
foreign key (idAudio) references audio(idAudio) on delete cascade,
constraint fk_favoritos primary key (idCliente,idAudio)
);

INSERT INTO idioma (description) VALUES 
('Español'), ('Inglés'), ('Francés'), ('Ruso'), ('Coreano'), ('Portugués'), ('Alemán');

INSERT INTO artista (nombreArtistico, imagen, genero, descripcion) VALUES 
('Nova Sky', 'nova.jpg', 'Synthwave', 'Viajero del tiempo musical'),
('The Coding Band', 'band.png', 'Math Rock', 'Ritmos complejos para mentes lógicas'),
('DJ Bit', 'dj.jpg', 'Electronic', 'El rey del 8-bit moderno'),
('Luna Eterna', 'luna.jpg', 'Indie Folk', 'Melodías bajo la luz de la luna'),
('Protocolo 7', 'p7.jpg', 'Industrial', 'Sonidos de una distopía cercana'),
('Elena Vox', 'elena.jpg', 'Jazz Fusion', 'La voz de terciopelo de Madrid'),
('Podcast Central', 'mic.jpg', 'Educativo', 'Divulgación científica y tecnológica');

INSERT INTO musico (idMusico, caracteristica) VALUES 
(1, 'Solista'), (2, 'Grupo'), (3, 'Solista'), (4, 'Solista'), (5, 'Grupo'), (6, 'Solista');
INSERT INTO podcaster (idPodcaster) VALUES (7);

INSERT INTO audio (nombre, duracion, archivo, tipo, nReproducciones) VALUES 
('Cyber Dream', '00:03:45', 'cyber.mp3', 'Cancion', 1500),
('Algoritmo Perfecto', '00:05:12', 'algo.mp3', 'Cancion', 890),
('Café de Medianoche', '00:04:20', 'cafe.mp3', 'Cancion', 2300),
('El Futuro de la IA', '00:45:00', 'podcast_ia.mp3', 'Podcast', 5000),
('Sinfonía Binaria', '00:03:10', 'bin.mp3', 'Cancion', 120),
('Historia de la Web', '00:30:00', 'web.mp3', 'Podcast', 3400),
('Metal Líquido', '00:04:05', 'metal.mp3', 'Cancion', 750);

INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES 
(4, 2, 7), (6, 1, 7);



INSERT INTO album (titulo, ano, genero, imagen, idMusico) VALUES 
('Neon City', '2023-01-01', 'Synthwave', 'neon.jpg', 1),
('Null Pointer Exception', '2022-11-15', 'Math Rock', 'null.jpg', 2),
('Lo-Fi Nights', '2023-05-20', 'Electronic', 'lofi.jpg', 3),
('Bosque Sonoro', '2021-08-10', 'Indie Folk', 'forest.jpg', 4),
('Error 404', '2024-02-14', 'Industrial', 'error.jpg', 5),
('Blue Note', '2020-12-01', 'Jazz', 'blue.jpg', 6),
('Singles 2024', '2024-01-01', 'Varios', 'singles.jpg', 1);

INSERT INTO cancion (idCancion, idAlbum, artistasInvitados) VALUES 
(1, 1, 'Laser Cat'), 
(2, 2, NULL), 
(3, 3, 'Relax Girl'), 
(5, 5, 'The Glitch'), 
(7, 7, 'Iron Man'); 

INSERT INTO cliente (nombre, apellidos, usuario, contrasena, fechaNacimiento, tipo, idIdioma) VALUES 
('Alan', 'Turing', 'enigma', 'pass1', '1912-06-23', 'Premium', 2),
('Ada', 'Lovelace', 'first_coder', 'pass2', '1815-12-10', 'Premium', 2),
('Linus', 'Torvalds', 'penguin_lord', 'pass3', '1969-12-28', 'Free', 7),
('Grace', 'Hopper', 'amazing_grace', 'pass4', '1906-12-09', 'Premium', 2),
('Hedy', 'Lamarr', 'wifi_queen', 'pass5', '1914-11-09', 'Free', 1),
('Guido', 'Van Rossum', 'python_father', 'pass6', '1956-01-31', 'Premium', 1),
('Margaret', 'Hamilton', 'apollo_code', 'pass7', '1936-08-17', 'Free', 2);

-- Premium
INSERT INTO premium (idCliente, fechaCaducidad) VALUES 
(1, '2026-12-31'), (2, '2026-12-31'), (4, '2027-01-15'), (6, '2026-06-30');

-- Playlists
INSERT INTO playlist (titulo, fechaCreacion, IdCliente) VALUES 
('Coding Beats', '2024-01-01', 1),
('Moonlight Melodies', '2024-01-05', 2),
('Debug Music', '2024-01-10', 3),
('Late Night Jazz', '2024-02-01', 4),
('Industrial Strength', '2024-02-15', 5),
('Folk Dreams', '2024-03-01', 6),
('Daily Podcasts', '2024-03-10', 7);

INSERT INTO playlist_canciones (idCancion, idPlaylist, fechaPlaylist_cancion) VALUES 
(1, 1, '2024-01-02'), (2, 1, '2024-01-02'), (3, 2, '2024-01-06'), 
(5, 3, '2024-01-11'), (7, 5, '2024-02-16'), (1, 3, '2024-01-12'), (2, 3, '2024-01-12');

INSERT INTO favoritos (idCliente, idAudio) VALUES 
(1, 1), (1, 4), (2, 3), (3, 2), (4, 6), (5, 7), (6, 5);
