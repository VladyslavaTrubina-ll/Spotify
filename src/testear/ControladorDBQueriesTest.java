package testear;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import controlador.ControladorDB;
import modelo.Audio;
import modelo.Cliente;
import modelo.Musico;
import modelo.Playlist;
import modelo.Podcaster;

/**
 * Consultas simples agrupadas para gestión BD.
 */
public class ControladorDBQueriesTest {

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
    public void testConsultasGenerales() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        ArrayList<String> artistas = db.sqlArtistas();
        assertNotNull(artistas);

        ArrayList<Audio> audios = db.obtenerAudios();
        assertNotNull(audios);

        ArrayList<Cliente> clientes = db.obtenerClientes();
        assertNotNull(clientes);

        ArrayList<Musico> musicos = db.obtenerMusicos();
        assertNotNull(musicos);

        ArrayList<Podcaster> podcasters = db.obtenerPodcasters();
        assertNotNull(podcasters);

        if (!clientes.isEmpty()) {
            int idCliente = clientes.get(0).getId();
            ArrayList<Playlist> playlists = db.obtenerPlaylists(idCliente);
            assertNotNull(playlists);

            ArrayList<Audio> favoritos = db.obtenerFavoritos(idCliente);
            assertNotNull(favoritos);
        }
    }
}
