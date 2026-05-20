package controlador;

import modelo.*;
import java.util.*;

public class GestorCliente {
	private ControladorDB controladordb;
	private Cliente clienteActual;
	private static final String NOMBRE_BD_DEFECTO = "spoty";

	public GestorCliente() {
		this(NOMBRE_BD_DEFECTO);
	}

	public GestorCliente(String nombreBD) {
		this.controladordb = new ControladorDB(nombreBD);
		this.clienteActual = null;
	}

	// ==================== AUTENTICACIÓN ====================

	/**
	 * Realiza el login de un cliente
	 */
	public Cliente login(String usuario, String contrasena) {
		if (controladordb.startConnection()) {
			ArrayList<Cliente> clientes = controladordb.obtenerClientes();
			
			for (Cliente c : clientes) {
				if (c.getUsuario().equals(usuario) && c.getContrasena().equals(contrasena)) {
					c.setPlaylistCliente(controladordb.obtenerPlaylists(c.getId()));
					this.clienteActual = c;
					// sincronizar id del cliente en el controlador de BD
					controladordb.setIdClienteActual(c.getId());
					controladordb.cerrarConexion();
					return c;
				}
			}
			controladordb.cerrarConexion();
		}
		return null;
	}

	/**
	 * Registra un nuevo cliente en el sistema
	 */
	public boolean registrarCliente(String nombre, String apellido, String usuario, String contrasena, 
			String fechaNacimiento, String idioma) {
		if (controladordb.startConnection()) {
			try {
				nombre = ControladorEntradaYSalida.letraMalluscula(nombre);
				apellido = ControladorEntradaYSalida.letraMalluscula(apellido);
				java.sql.Date fechaNac = java.sql.Date.valueOf(fechaNacimiento);
				boolean resultado = controladordb.sqlCrear(nombre, apellido, usuario, contrasena, fechaNac, idioma);
				controladordb.cerrarConexion();
				return resultado;
			} catch (Exception e) {
				System.out.println("Error en registro: " + e.getMessage());
				controladordb.cerrarConexion();
			}
		}
		return false;
	}

	/**
	 * Verifica si un cliente es administrador
	 */
	public boolean esAdmin(Cliente c) {
		return c.getUsuario().equals("admin") && c.getContrasena().equals("admin");
	}

	// ==================== MÉTODOS DE CLIENTE ====================

	/**
	 * Obtiene el cliente actualmente logueado
	 */
	public Cliente getClienteActual() {
		return this.clienteActual;
	}

	/**
	 * Obtiene todos los artistas disponibles
	 */
	public ArrayList<String> obtenerArtistas() {
		if (controladordb.startConnection()) {
			ArrayList<String> artistas = controladordb.sqlArtistas();
			controladordb.cerrarConexion();
			return artistas;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene todos los músicos con sus datos
	 */
	public ArrayList<Musico> obtenerMusicos() {
		if (controladordb.startConnection()) {
			ArrayList<Musico> musicos = controladordb.obtenerMusicos();
			controladordb.cerrarConexion();
			return musicos;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene el discografía de un artista
	 */
	public ArrayList<Album> obtenerDiscografia(String nombreArtista) {
		if (controladordb.startConnection()) {
			ArrayList<Album> albums = controladordb.obtenerAlbum(nombreArtista);
			controladordb.cerrarConexion();
			return albums;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene las canciones de un álbum
	 */
	public ArrayList<Cancion> obtenerCancionesAlbum(String nombreAlbum) {
		if (controladordb.startConnection()) {
			ArrayList<Cancion> canciones = controladordb.obtenerCanciones(nombreAlbum);
			controladordb.cerrarConexion();
			return canciones;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene todos los podcasters
	 */
	public ArrayList<Podcaster> obtenerPodcasters() {
		if (controladordb.startConnection()) {
			ArrayList<Podcaster> podcasters = controladordb.obtenerPodcasters();
			controladordb.cerrarConexion();
			return podcasters;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene los podcasts de un podcaster
	 */
	public ArrayList<Podcast> obtenerPodcasts(String nombrePodcaster) {
		if (controladordb.startConnection()) {
			ArrayList<Podcast> podcasts = controladordb.obtenerPodcasts(nombrePodcaster);
			controladordb.cerrarConexion();
			return podcasts;
		}
		return new ArrayList<>();
	}

	// ==================== MÉTODOS DE PLAYLISTS ====================

	/**
	 * Obtiene las playlists del cliente actual
	 */
	public ArrayList<Playlist> obtenerPlaylistsCliente() {
		if (clienteActual == null) {
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<Playlist> playlists = controladordb.obtenerPlaylists(clienteActual.getId());
			controladordb.cerrarConexion();
			return playlists;
		}
		return new ArrayList<>();
	}

	/**
	 * Crea una nueva playlist (valida límite de 3 para usuarios Free)
	 */
	public boolean crearPlaylist(String nombrePlaylist) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			// Validar si es usuario Free y tiene límite de 3 playlists
			if (!clienteActual.isEsPremium()) {
				if (!controladordb.puedeCrearPlaylist(clienteActual.getId(), false)) {
					System.out.println("Error: Usuario Free limitado a 3 playlists");
					controladordb.cerrarConexion();
					return false;
				}
			}

			boolean resultado = controladordb.anadirPlaylist(nombrePlaylist);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Elimina una playlist del cliente actual
	 */
	public boolean eliminarPlaylist(String nombrePlaylist) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.borrarPlaylist(nombrePlaylist);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Agrega una canción a una playlist
	 */
	public boolean agregarCancionPlaylist(String nombreCancion, String nombrePlaylist) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.anadirCancionPlaylist(nombreCancion, nombrePlaylist);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Obtiene las canciones de una playlist del cliente
	 */
	public ArrayList<Cancion> obtenerCancionesPlaylist(int idPlaylist) {
		if (controladordb.startConnection()) {
			ArrayList<Cancion> canciones = controladordb.obtenerCancionesPlaylist(idPlaylist);
			controladordb.cerrarConexion();
			return canciones;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene todos los audios disponibles para el reproductor
	 */
	public ArrayList<Audio> obtenerAudiosDisponibles() {
		if (controladordb.startConnection()) {
			ArrayList<Audio> audios = controladordb.obtenerAudios();
			controladordb.cerrarConexion();
			return audios;
		}
		return new ArrayList<>();
	}

	// ==================== MÉTODOS DE REPRODUCCIÓN ====================

	/**
	 * Valida y registra la reproducción de una canción (10 min para Free users)
	 */
	public boolean reproducirCancion(int idAudio) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean puedeReproducir = controladordb.puedeReproducirCancion(clienteActual.getId(), idAudio, 
					clienteActual.isEsPremium());

			if (!puedeReproducir) {
				System.out.println("Error: Usuario Free debe esperar 10 minutos entre canciones");
				controladordb.cerrarConexion();
				return false;
			}

			// Registrar la reproducción
			controladordb.registrarUltimaReproduccion(clienteActual.getId(), idAudio);
			controladordb.cerrarConexion();
			return true;
		}
		return false;
	}

	/**
	 * Cambia el cliente a Premium
	 */
	public boolean actualizarAPremium() {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		clienteActual.setEsPremium(true);
		if (controladordb.startConnection()) {
			boolean resultado = controladordb.actualizarCliente(clienteActual);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	// ==================== MÉTODOS DEL ADMINISTRADOR ====================

	/**
	 * Crea un nuevo músico (Solo Administrador)
	 */
	public boolean crearMusico(Musico musico) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede crear músicos");
			return false;
		}

		if (controladordb.startConnection()) {
			controladordb.insertarMusico(musico);
			controladordb.cerrarConexion();
			return true;
		}
		return false;
	}

	/**
	 * Actualiza los datos de un artista (Solo Administrador)
	 */
	public boolean actualizarArtista(int idArtista, String nombre, String genero, String descripcion, String imagen) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede actualizar artistas");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.actualizarArtista(idArtista, nombre, genero, descripcion, imagen);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Elimina un artista (Solo Administrador)
	 */
	public boolean eliminarArtista(int idArtista) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede eliminar artistas");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.eliminarArtista(idArtista);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Crea un nuevo álbum (Solo Administrador)
	 */
	public boolean crearAlbum(Album album) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede crear álbumes");
			return false;
		}

		if (controladordb.startConnection()) {
			controladordb.insertarAlbum(album);
			controladordb.cerrarConexion();
			return true;
		}
		return false;
	}

	/**
	 * Actualiza los datos de un álbum (Solo Administrador)
	 */
	public boolean actualizarAlbum(int idAlbum, String titulo, String ano, String genero, String imagen, int idMusico) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede actualizar álbumes");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.actualizarAlbum(idAlbum, titulo, ano, genero, imagen, idMusico);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Elimina un álbum (Solo Administrador)
	 */
	public boolean eliminarAlbum(int idAlbum) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede eliminar álbumes");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.eliminarAlbum(idAlbum);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Crea una nueva canción (Solo Administrador)
	 */
	public boolean crearCancion(Cancion cancion) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede crear canciones");
			return false;
		}

		if (controladordb.startConnection()) {
			controladordb.insertarCancion(cancion);
			controladordb.cerrarConexion();
			return true;
		}
		return false;
	}

	/**
	 * Actualiza los datos de una canción (Solo Administrador)
	 */
	public boolean actualizarCancion(int idCancion, String nombre, String archivo, String duracion, 
			int idAlbum, String artistasInvitados) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede actualizar canciones");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.actualizarCancion(idCancion, nombre, archivo, duracion, idAlbum, 
					artistasInvitados);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Elimina una canción (Solo Administrador)
	 */
	public boolean eliminarCancion(int idCancion) {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede eliminar canciones");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.eliminarCancion(idCancion);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Obtiene estadísticas de canciones más escuchadas
	 */
	public ArrayList<StastisticaCancion> obtenerEstadisticasCanciones() {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede ver estadísticas");
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<StastisticaCancion> stats = controladordb.obtenerstatcanciones();
			controladordb.cerrarConexion();
			return stats;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene estadísticas de audios más escuchados
	 */
	public ArrayList<StatisticaAudio> obtenerEstadisticasAudio() {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede ver estadísticas");
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<StatisticaAudio> stats = controladordb.obtenerstataudio();
			controladordb.cerrarConexion();
			return stats;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene estadísticas de podcasts más escuchados
	 */
	public ArrayList<StatisticaPodcast> obtenerEstadisticasPodcast() {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede ver estadísticas");
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<StatisticaPodcast> stats = controladordb.obtenerstatPodcast();
			controladordb.cerrarConexion();
			return stats;
		}
		return new ArrayList<>();
	}

	/**
	 * Obtiene estadísticas de playlists más escuchadas
	 */
	public ArrayList<StatisticaPlaylist> obtenerEstadisticasPlaylist() {
		if (clienteActual == null || !esAdmin(clienteActual)) {
			System.out.println("Error: Solo administrador puede ver estadísticas");
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<StatisticaPlaylist> stats = controladordb.obtenerstatPlaylist();
			controladordb.cerrarConexion();
			return stats;
		}
		return new ArrayList<>();
	}

	// ==================== MÉTODOS DE FAVORITOS ====================

	/**
	 * Obtiene todos los audios favoritos del cliente actual
	 */
	public ArrayList<Audio> obtenerFavoritosCliente() {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return new ArrayList<>();
		}

		if (controladordb.startConnection()) {
			ArrayList<Audio> favoritos = controladordb.obtenerFavoritos(clienteActual.getId());
			controladordb.cerrarConexion();
			return favoritos;
		}
		return new ArrayList<>();
	}

	/**
	 * Verifica si un audio está en favoritos del cliente actual
	 */
	public boolean estaEnFavoritosCliente(int idAudio) {
		if (clienteActual == null) {
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.estaEnFavoritos(clienteActual.getId(), idAudio);
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Agrega un audio a favoritos del cliente actual
	 */
	public boolean agregarAFavoritosCliente(int idAudio) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.agregarAFavoritos(clienteActual.getId(), idAudio);
			if (resultado) {
				System.out.println("♥ Añadido a favoritos");
			}
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Elimina un audio de favoritos del cliente actual
	 */
	public boolean eliminarDeFavoritosCliente(int idAudio) {
		if (clienteActual == null) {
			System.out.println("Error: No hay cliente logueado");
			return false;
		}

		if (controladordb.startConnection()) {
			boolean resultado = controladordb.eliminarDeFavoritos(clienteActual.getId(), idAudio);
			if (resultado) {
				System.out.println("♡ Eliminado de favoritos");
			}
			controladordb.cerrarConexion();
			return resultado;
		}
		return false;
	}

	/**
	 * Cierra la sesión del cliente actual
	 */
	public void logout() {
		this.clienteActual = null;
		controladordb.cerrarConexion();
	}

}
