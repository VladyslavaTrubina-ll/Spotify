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

/**
 * Reglas de negocio y relaciones: playlists, favoritos y reproducción FREE/Premium.
 */
public class ControladorDBBusinessRulesTest {

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
    public void testReglaPuedeCrearPlaylist() {
        // Esta parte no necesita BD para premium
        assertTrue(db.puedeCrearPlaylist(1, true));

        Assume.assumeTrue("Se requiere conexión a spoty para regla FREE", conectado);

        ArrayList<Cliente> clientes = db.obtenerClientes();
        Assume.assumeTrue("Debe existir al menos un cliente para validar FREE", clientes != null && !clientes.isEmpty());

        int idCliente = clientes.get(0).getId();
        int total = db.contarPlaylistsUsuario(idCliente);
        boolean puedeFree = db.puedeCrearPlaylist(idCliente, false);

        if (total >= 0) {
            assertEquals("La regla FREE debe ser total < 3", total < 3, puedeFree);
        }
    }

    @Test
    public void testFavoritosYControlReproduccion() {
        Assume.assumeTrue("Se requiere conexión a spoty", conectado);

        ArrayList<Cliente> clientes = db.obtenerClientes();
        ArrayList<Audio> audios = db.obtenerAudios();
        Assume.assumeTrue(clientes != null && !clientes.isEmpty());
        Assume.assumeTrue(audios != null && !audios.isEmpty());

        int idCliente = clientes.get(0).getId();
        int idAudio = audios.get(0).getId();

        // Favoritos
        boolean yaEstaba = db.estaEnFavoritos(idCliente, idAudio);
        if (!yaEstaba) {
            assertTrue("Debe poder agregar a favoritos", db.agregarAFavoritos(idCliente, idAudio));
        }
        assertTrue("Debe quedar marcado en favoritos", db.estaEnFavoritos(idCliente, idAudio));
        assertTrue("Debe poder eliminar de favoritos", db.eliminarDeFavoritos(idCliente, idAudio));

        // Reproducción FREE: tras registrar reproducción, no debería poder reproducir inmediatamente
        assertTrue("Debe registrar última reproducción", db.registrarUltimaReproduccion(idCliente, idAudio));
        assertFalse("FREE no debe poder reproducir inmediatamente el mismo audio", db.puedeReproducirCancion(idCliente, idAudio, false));
        assertTrue("Premium siempre puede reproducir", db.puedeReproducirCancion(idCliente, idAudio, true));
    }
}
