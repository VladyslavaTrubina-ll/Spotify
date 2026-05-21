package testear;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;

import controlador.ControladorDB;

/**
 * JUnit4 tests for ControladorDB. Tests that require a live DB are skipped
 * when the connection cannot be established (using Assume.assumeTrue).
 */
public class ControladorDBTest {

	private static ControladorDB controlador;
	private static boolean conectado;

	@BeforeClass
	public static void setUpBeforeClass() {
		controlador = new ControladorDB("spoty");
		conectado = controlador.startConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (controlador != null && conectado) {
			controlador.closeConnection();
		}
	}

	@Test
	public void testPremiumSiemprePuedeCrearPlaylist() {
		ControladorDB c = new ControladorDB("no_importa");
		assertTrue("Un usuario premium debe poder crear playlist siempre", c.puedeCrearPlaylist(1, true));
	}

	@Test
	public void testComportamientoConConexionCerrada() {
		ControladorDB controladorCerrado = new ControladorDB("cine_daw");

		assertFalse("sqlLogin debe retornar false si no hay conexión",
				controladorCerrado.sqlLogin("admin", "admin"));

		ArrayList<String> artistas = controladorCerrado.sqlArtistas();
		assertNotNull("sqlArtistas debe devolver lista no nula aun sin conexión", artistas);
		assertTrue("Lista debe estar vacía cuando no hay conexión", artistas.isEmpty());
	}

	@Test
	public void testSqlArtistasOrdenYContenido_siHayConexion() {
		Assume.assumeTrue("Se necesita conexión a BD para este test", conectado);

		ArrayList<String> artistas = controlador.sqlArtistas();
		assertNotNull("La lista de artistas no debe ser null", artistas);
		if (artistas.size() > 1) {
			assertTrue("La lista debe venir ordenada alfabéticamente",
					artistas.get(0).compareToIgnoreCase(artistas.get(1)) <= 0);
		}
	}

	@Test
	public void testSqlCrearConIdiomaInexistente_siHayConexion() {
		Assume.assumeTrue(conectado);
		Date fechaNac = Date.valueOf("2000-01-01");
		boolean creado = controlador.sqlCrear("Test", "Error", "alias_raro", "pass", fechaNac,
				"IdiomaIninventableXYZ");
		assertFalse("No debe crearse un cliente con un idioma inválido", creado);
	}

	@Test
	public void testAnadirPlaylistSinUsuarioLogueado() {
		ControladorDB c = new ControladorDB("spoty");
		c.setIdClienteActual(-1);
		// Sin conexión y sin usuario logueado debe devolver false
		assertFalse(c.anadirPlaylist("Playlist Huérfana"));
	}
}
