package testear;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import controlador.ControladorDB;
import modelo.Album;
import modelo.Artista;
import modelo.Cancion;
import modelo.Musico;

/**
 * Updates y deletes agrupados para CRUD administrativo.
 */
public class ControladorDBUpdatesDeletesTest {

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
    public void testActualizarYEliminarArtistaAlbumCancion() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        String sufijo = String.valueOf(System.currentTimeMillis());

        // Crear artista (músico)
        String nombreMusico = "upd_mus_" + sufijo;
        db.insertarMusico(new Musico(0, nombreMusico, "Pop", "desc", "img.png", "Solista"));
        Artista artista = db.obtenerArtistaPorNombre(nombreMusico);
        assertNotNull(artista);

        // Update artista
        boolean artistaUpdated = db.actualizarArtista(
                artista.getId(),
                nombreMusico + "_edit",
                "Jazz",
                "desc edit",
                "new.png");
        assertTrue("Debe actualizar artista", artistaUpdated);

        Artista artistaEditado = db.obtenerArtistaPorNombre(nombreMusico + "_edit");
        assertNotNull("Debe recuperar artista actualizado", artistaEditado);

        // Crear album
        String tituloAlbum = "upd_alb_" + sufijo;
        db.insertarAlbum(new Album(0, tituloAlbum, "2023", "Pop", "a.png", artistaEditado.getId()));
        Album album = db.obtenerAlbumPorTitulo(tituloAlbum);
        assertNotNull(album);

        // Update album
        boolean albumUpdated = db.actualizarAlbum(
                album.getId(),
                tituloAlbum + "_edit",
                "2025",
                "Rock",
                "b.png",
                artistaEditado.getId());
        assertTrue("Debe actualizar álbum", albumUpdated);

        Album albumEditado = db.obtenerAlbumPorTitulo(tituloAlbum + "_edit");
        assertNotNull("Debe recuperar álbum actualizado", albumEditado);

        // Crear canción
        String nombreCancion = "upd_can_" + sufijo;
        db.insertarCancion(new Cancion(0, nombreCancion, "c.mp3", 210, 0, albumEditado.getId(), "Colab", "Cancion"));
        Cancion cancion = db.obtenerCancionPorNombre(nombreCancion);
        assertNotNull(cancion);

        // Update canción
        boolean cancionUpdated = db.actualizarCancion(
                cancion.getId(),
                nombreCancion + "_edit",
                "c2.mp3",
                "00:03:40",
                albumEditado.getId(),
                "Nadie");
        assertTrue("Debe actualizar canción", cancionUpdated);

        Cancion cancionEditada = db.obtenerCancionPorNombre(nombreCancion + "_edit");
        assertNotNull("Debe recuperar canción actualizada", cancionEditada);

        // Deletes
        assertTrue("Debe eliminar canción", db.eliminarCancion(cancionEditada.getId()));
        assertTrue("Debe eliminar álbum", db.eliminarAlbum(albumEditado.getId()));
        assertTrue("Debe eliminar artista", db.eliminarArtista(artistaEditado.getId()));
    }
}
