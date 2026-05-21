package testear;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import controlador.ControladorDB;
import modelo.EstadisticaAudio;
import modelo.EstadisticaCancion;
import modelo.EstadisticaPlaylist;
import modelo.EstadisticaPodcast;

/**
 * Estadísticas agrupadas (4 tipos exigidos por rúbrica).
 */
public class ControladorDBStatsTest {

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
    public void testObtenerCuatroTiposDeEstadisticas() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        ArrayList<EstadisticaCancion> canciones = db.obtenerEstadisticasCanciones();
        ArrayList<EstadisticaAudio> audios = db.obtenerEstadisticasAudio();
        ArrayList<EstadisticaPodcast> podcasts = db.obtenerEstadisticasPodcast();
        ArrayList<EstadisticaPlaylist> playlists = db.obtenerEstadisticasPlaylist();

        assertNotNull(canciones);
        assertNotNull(audios);
        assertNotNull(podcasts);
        assertNotNull(playlists);
    }
}
