package testear;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import controlador.ControladorDB;
import modelo.Album;
import modelo.Artista;
import modelo.Cancion;
import modelo.Podcast;
import modelo.Podcaster;
import modelo.Cliente;
import modelo.Musico;

/**
 * Inserciones agrupadas para gestión BD (cliente, artista, album, canción, podcast).
 */
public class ControladorDBInsertsTest {

    private static ControladorDB db;
    private static boolean conectado;

    @BeforeClass
    public static void setUpClass() {
        db = new ControladorDB("spoty");
        conectado = db.startConnection();
    }

    @AfterClass
    public static void tearDownClass() {
        if (db != null && conectado) {
            db.closeConnection();
        }
    }

    @Test
    public void testInsertClienteYLogin() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        String sufijo = String.valueOf(System.currentTimeMillis());
        String usuario = "u_test_" + sufijo;
        boolean creado = db.sqlCrear("Nombre", "Apellido", usuario, "1234", Date.valueOf("2000-01-01"), "1");
        assertTrue("Debe insertar cliente con idioma válido", creado);

        boolean loginOk = db.sqlLogin(usuario, "1234");
        assertTrue("Debe permitir login tras crear el cliente", loginOk);
        assertTrue("Debe setear idClienteActual", db.getIdClienteActual() > 0);
    }

    @Test
    public void testInsertCadenaMusicoAlbumCancionYPodcast() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        String sufijo = String.valueOf(System.currentTimeMillis());

        // --- Músico -> Álbum -> Canción
        String nombreMusico = "mus_test_" + sufijo;
        Musico musico = new Musico(0, nombreMusico, "Rock", "desc", "img.png", "Solista");
        db.insertarMusico(musico);
        assertTrue("Debe existir artista tras insertar músico", db.existeArtista(nombreMusico));

        Artista artistaMusico = db.obtenerArtistaPorNombre(nombreMusico);
        assertNotNull("Debe poder obtener el artista insertado", artistaMusico);

        String tituloAlbum = "alb_test_" + sufijo;
        Album album = new Album(0, tituloAlbum, "2024", "Rock", "alb.png", artistaMusico.getId());
        db.insertarAlbum(album);
        assertTrue("Debe existir álbum tras inserción", db.existeAlbum(tituloAlbum));

        Album albumInsertado = db.obtenerAlbumPorTitulo(tituloAlbum);
        assertNotNull("Debe poder obtener álbum insertado", albumInsertado);

        String nombreCancion = "can_test_" + sufijo;
        Cancion cancion = new Cancion(0, nombreCancion, "song.mp3", 180, 0, albumInsertado.getId(), "Invitado", "Cancion");
        db.insertarCancion(cancion);
        assertTrue("Debe existir canción tras inserción", db.existeCancion(nombreCancion));

        Cancion cancionInsertada = db.obtenerCancionPorNombre(nombreCancion);
        assertNotNull("Debe poder obtener canción insertada", cancionInsertada);

        // --- Podcaster -> Podcast
        String nombrePodcaster = "podc_test_" + sufijo;
        Podcaster podcaster = new Podcaster(0, nombrePodcaster, "Tecnologia", "desc pod", "pod.png");
        db.insertarPodcaster(podcaster);
        assertTrue("Debe existir podcaster tras inserción", db.existePodcaster(nombrePodcaster));

        Artista artistaPodcaster = db.obtenerArtistaPorNombre(nombrePodcaster);
        assertNotNull("Debe obtener artista del podcaster insertado", artistaPodcaster);

        String nombrePodcast = "pod_test_" + sufijo;
        Podcast podcast = new Podcast(0, nombrePodcast, "pod.mp3", 600, 0, artistaPodcaster.getId(), 2, "Podcast");
        db.insertarPodcast(podcast);
        assertTrue("Debe existir podcast tras inserción", db.existePodcast(nombrePodcast));

        // Cleanup (mejor esfuerzo)
        if (cancionInsertada != null) {
            db.eliminarCancion(cancionInsertada.getId());
        }
        if (albumInsertado != null) {
            db.eliminarAlbum(albumInsertado.getId());
        }

        ArrayList<Podcast> podcastsDelPodcaster = db.obtenerPodcasts(nombrePodcaster);
        if (podcastsDelPodcaster != null) {
            for (Podcast p : podcastsDelPodcaster) {
                if (nombrePodcast.equals(p.getNombreAudio())) {
                    db.eliminarPodcast(p.getId());
                }
            }
        }

        if (artistaPodcaster != null) {
            db.eliminarPodcaster(artistaPodcaster.getId());
            db.eliminarArtista(artistaPodcaster.getId());
        }
        if (artistaMusico != null) {
            db.eliminarArtista(artistaMusico.getId());
        }
    }

    @Test
    public void testInsertarClienteObjeto() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        String sufijo = String.valueOf(System.currentTimeMillis());
        Cliente c = new Cliente();
        c.setNombre("N" + sufijo);
        c.setApellido("A" + sufijo);
        c.setUsuario("obj_" + sufijo);
        c.setContrasena("1234");
        c.setFecNac("2001-01-01");
        c.setIdioma("1");
        c.setEsPremium(false);

        db.insertarCliente(c);
        boolean login = db.sqlLogin(c.getUsuario(), c.getContrasena());
        assertTrue("Cliente insertado por objeto debe poder loguear", login);
    }
}
