package vista;

import controlador.ControladorDB;
import java.sql.Date;
import java.util.ArrayList;
import modelo.Album;
import modelo.Cancion;

public class Launcher {
    public static void main(String[] args) {
        ControladorDB controladorDB = new ControladorDB("spoty");

        if (!controladorDB.startConnection()) {
            System.out.println("✗ No se pudo establecer conexión a la base de datos.");
            return;
        }

        System.out.println("✓ Conexión a la base de datos establecida.");
        try {
            // Listar artistas
            System.out.println("\n--- Test: Listando artistas ---");
            ArrayList<String> artistas = controladorDB.sqlArtistas();
            System.out.println("Artistas encontrados: " + artistas.size());
            for (String artista : artistas) {
                System.out.println(" - " + artista);
            }

            // Login
            System.out.println("\n--- Test: Intentando login (juanito88) ---");
            boolean loginExitoso = controladorDB.iniciarSesion("juanito88", "hash_pass_123");
            System.out.println("Login juanito88: " + loginExitoso);

            // Crear y eliminar playlist
            System.out.println("\n--- Test: Crear y eliminar playlist ---");
            String testPlaylist = "Launcher_Test_Playlist";
            boolean playlistCreada = controladorDB.anadirPlaylist(testPlaylist);
            System.out.println("Playlist creada: " + playlistCreada);

            boolean cancionAnadida = controladorDB.anadirCancionPlaylist("Talk", testPlaylist);
            System.out.println("Añadir canción a playlist (Talk): " + cancionAnadida);

            boolean playlistBorrada = controladorDB.borrarPlaylist(testPlaylist);
            System.out.println("Playlist borrada: " + playlistBorrada);

            // Consultas por artista/album
            System.out.println("\n--- Test: Albums y canciones sample ---");
            ArrayList<Album> albums = controladorDB.obtenerAlbum("Salvatore Ganacci");
            System.out.println("Albums de 'Salvatore Ganacci': " + albums.size());
            for (Album album : albums) {
                System.out.println(" - " + album);
            }

            if (!albums.isEmpty()) {
                String titulo = albums.get(0).getTitulo();
                ArrayList<Cancion> canciones = controladorDB.obtenerCanciones(titulo);
                System.out.println("Canciones en album '" + titulo + "': " + canciones.size());
                for (Cancion cancion : canciones) {
                    System.out.println("   * " + cancion);
                }
            }

            // Registrar cliente de prueba
            System.out.println("\n--- Test: Registrar cliente ---");
            Date fechaNacimiento = Date.valueOf("1995-05-20");
            long timestamp = System.currentTimeMillis();
            String uniqueUser = "test_user_" + timestamp;
            boolean clienteCreado = controladorDB.sqlCrear("TestName", "TestLast", uniqueUser, "test_pass", fechaNacimiento, "1");
            System.out.println("Cliente creado: " + clienteCreado);

            // Estadísticas (sólo tamaño de colecciones para ver que consultas funcionan)
            System.out.println("\n--- Test: Estadísticas ---");
            System.out.println("Top canciones: " + controladorDB.obtenerstatcanciones().size());
            System.out.println("Top audios: " + controladorDB.obtenerstataudio().size());
            System.out.println("Top podcasts: " + controladorDB.obtenerstatPodcast().size());
            System.out.println("Top playlists: " + controladorDB.obtenerstatPlaylist().size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            controladorDB.cerrarConexion();
            System.out.println("\n✓ Conexión cerrada.");
        }
    }

}
