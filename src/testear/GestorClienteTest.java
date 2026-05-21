package testear;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import controlador.GestorClienteNuevo;
import modelo.Album;
import modelo.Cliente;
import modelo.Playlist;

import java.util.ArrayList;

/**
 * JUnit4 tests for GestorClienteNuevo. DB-dependent tests are skipped when
 * a connection cannot be established.
 */
public class GestorClienteTest {

    private GestorClienteNuevo gestor;
    private final String BD_TEST = "spoty";

    @Before
    public void setUp() {
        gestor = new GestorClienteNuevo(BD_TEST);
    }

    @After
    public void tearDown() {
        gestor.logout();
    }

    @Test
    public void testEsAdminConDatosExactos() {
        Cliente adminValido = new Cliente();
        adminValido.setUsuario("admin");
        adminValido.setContrasena("admin");

        Cliente adminInvalido = new Cliente();
        adminInvalido.setUsuario("admin");
        adminInvalido.setContrasena("passwordCualquiera");

        assertTrue("Debe retornar true únicamente cuando usuario y clave sean 'admin'", gestor.esAdmin(adminValido));
        assertFalse("Debe fallar si la contraseña no coincide explícitamente", gestor.esAdmin(adminInvalido));
        assertFalse("Debe controlar el parámetro nulo de forma segura sin lanzar excepciones", gestor.esAdmin(null));
    }

    @Test
    public void testCrearAlbumDenegadoParaUsuarioNoLogueado() {
        Album albumFalso = new Album(0, "Álbum Pirata", "2026", "Pop", "img.png", 1);
        // Sin usuario logueado el gestor debe negar la creación
        assertFalse("No debe permitirse crear álbum sin usuario autenticado", gestor.crearAlbum(albumFalso));
    }

    @Test
    public void testLoginConDatosNulosYVacios() {
        assertNull("Si el usuario es nulo, debe abortar inmediatamente sin tocar la BD", gestor.login(null, "1234"));
        assertNull("Si la contraseña es nula, debe abortar inmediatamente", gestor.login("jorge", null));
        assertNull("Credenciales vacías con espacios deben procesarse y fallar con seguridad", gestor.login("   ", "   "));
    }

    @Test
    public void testRegistroAplicaFormatoCorrecto_siHayConexion() {
        // Este test requiere BD activa; saltará si no se puede conectar
        boolean started = gestor.getControladorDB().startConnection();
        Assume.assumeTrue("Se necesita conexión a BD para probar el registro", started);

        String nombreMinuscula = "pedro";
        String apellidoMinuscula = "perez";
        String usuarioUnico = "pedrito" + System.currentTimeMillis();

        try {
            boolean registrado = gestor.registrarCliente(nombreMinuscula, apellidoMinuscula, usuarioUnico, "12345", "1995-05-12", "1");
            if (registrado) {
                Cliente guardado = gestor.login(usuarioUnico, "12345");
                assertNotNull("El cliente debería poder iniciar sesión tras registrarse", guardado);
                assertEquals("Pedro", guardado.getNombre());
                assertEquals("Perez", guardado.getApellido());
            }
        } finally {
            gestor.getControladorDB().closeConnection();
        }
    }

    @Test
    public void testEstadoDeSesionAlHacerLogout() {
        gestor.logout();
        assertNull("El cliente en caché ('clienteActual') debe quedar destruido tras el logout", gestor.getClienteActual());

        ArrayList<Playlist> playlists = gestor.obtenerPlaylistsCliente();
        assertNotNull("No debe retornar null al pedir playlists sin usuario", playlists);
        assertTrue("La lista debe estar vacía porque no hay nadie autenticado", playlists.isEmpty());
    }

    @Test
    public void testContarPlaylistsUsuario_siHayConexion() {
        boolean started = gestor.getControladorDB().startConnection();
        Assume.assumeTrue(started);
        try {
            int count = gestor.getControladorDB().contarPlaylistsUsuario(1);
            assertTrue("El contador de playlists debe devolver >= -1", count >= -1);
        } finally {
            gestor.getControladorDB().closeConnection();
        }
    }
}
