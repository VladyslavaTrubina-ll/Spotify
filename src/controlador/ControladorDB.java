package controlador;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import modelo.*;

public class ControladorDB {

    private Connection conect;
    private String nombreDB;
	private int idClienteActual = -1;
  
    public ControladorDB(String nombreDB) {
		this.nombreDB = nombreDB;
	}
  
    public ControladorDB() {
	}
  
  //Iniciar conexion
	public boolean startConnection() {
		boolean conexionestablecida = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conect = DriverManager.getConnection("jdbc:mysql://localhost/" + this.nombreDB + "?serverTimezone=UTC&useSSL=false", "root", "");
			conexionestablecida = true;
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontró la librería de sql");
		} catch (SQLException e) {
			System.out.println("No se pudo conectar a la BD " + this.nombreDB + ": " + e.getMessage());
		}
		return conexionestablecida;
	}

	public boolean iniciarSesion(String alias, String contrasenaUsuario) {
		return sqlLogin(alias, contrasenaUsuario);
	}

	public boolean sqlLogin(String nombreUsuario, String contrasenaUsuario) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT idCliente FROM cliente WHERE usuario = ? AND contrasena = ?";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombreUsuario);
			ps.setString(2, contrasenaUsuario);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					idClienteActual = rs.getInt("idCliente");
					return true;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error en login: " + e.getMessage());
		}
		return false;
	}

	public boolean sqlCrear(String nombre, String apellido, String alias, String contrasenaUsuario, Date fechaNacimiento,
			String idioma) {
		if (!hayConexion()) {
			return false;
		}

		Integer idIdioma = resolverIdioma(idioma);
		if (idIdioma == null) {
			return false;
		}

		String sql = "INSERT INTO cliente (nombre, apellidos, usuario, contrasena, fechaNacimiento, idIdioma) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombre);
			ps.setString(2, apellido);
			ps.setString(3, alias);
			ps.setString(4, contrasenaUsuario);
			ps.setDate(5, fechaNacimiento);
			ps.setInt(6, idIdioma);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("No se pudo crear el cliente: " + e.getMessage());
			return false;
		}
	}

	public ArrayList<String> sqlArtistas() {
		ArrayList<String> artistas = new ArrayList<>();
		if (!hayConexion()) {
			return artistas;
		}

		String sql = "SELECT nombreArtistico FROM artista ORDER BY nombreArtistico";
		try (PreparedStatement ps = conect.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				artistas.add(rs.getString("nombreArtistico"));
			}
		} catch (SQLException e) {
			System.out.println("No se pudieron cargar artistas: " + e.getMessage());
		}
		return artistas;
	}

	public ResultSet datoArtista() {
		if (!hayConexion()) {
			return null;
		}

		String sql = "SELECT idArtista, nombreArtistico, imagen, genero, descripcion FROM artista ORDER BY nombreArtistico";
		try {
			PreparedStatement ps = conect.prepareStatement(sql);
			return ps.executeQuery();
		} catch (SQLException e) {
			System.out.println("No se pudieron obtener datos de artistas: " + e.getMessage());
			return null;
		}
	}

	public boolean anadirPlaylist(String nombrePlaylist) {
		if (!hayConexion() || idClienteActual <= 0) {
			return false;
		}

		String sql = "INSERT INTO playlist (titulo, fechaCreacion, idCliente) VALUES (?, CURRENT_DATE, ?)";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombrePlaylist);
			ps.setInt(2, idClienteActual);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("No se pudo crear playlist: " + e.getMessage());
			return false;
		}
	}

	public boolean borrarPlaylist(String nombrePlaylist) {
		if (!hayConexion() || idClienteActual <= 0) {
			return false;
		}

		String sql = "DELETE FROM playlist WHERE titulo = ? AND idCliente = ?";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombrePlaylist);
			ps.setInt(2, idClienteActual);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("No se pudo borrar playlist: " + e.getMessage());
			return false;
		}
	}

	public boolean anadirCancionPlaylist(String nombreCancion, String nombrePlaylist) {
		if (!hayConexion() || idClienteActual <= 0) {
			return false;
		}

		String sqlPlaylist = "SELECT idPlaylist FROM playlist WHERE titulo = ? AND idCliente = ?";
		String sqlCancion = "SELECT c.idCancion FROM cancion c INNER JOIN audio a ON c.idCancion = a.idAudio WHERE a.nombre = ?";
		String sqlInsert = "INSERT INTO playlist_canciones (idCancion, idPlaylist, fechaPlaylist_cancion) VALUES (?, ?, CURRENT_DATE)";

		try (
				PreparedStatement psPlaylist = conect.prepareStatement(sqlPlaylist);
				PreparedStatement psCancion = conect.prepareStatement(sqlCancion);
				PreparedStatement psInsert = conect.prepareStatement(sqlInsert)) {

			psPlaylist.setString(1, nombrePlaylist);
			psPlaylist.setInt(2, idClienteActual);
			Integer idPlaylist = null;
			try (ResultSet rsPlaylist = psPlaylist.executeQuery()) {
				if (rsPlaylist.next()) {
					idPlaylist = rsPlaylist.getInt("idPlaylist");
				}
			}

			if (idPlaylist == null) {
				return false;
			}

			psCancion.setString(1, nombreCancion);
			Integer idCancion = null;
			try (ResultSet rsCancion = psCancion.executeQuery()) {
				if (rsCancion.next()) {
					idCancion = rsCancion.getInt("idCancion");
				}
			}

			if (idCancion == null) {
				return false;
			}

			psInsert.setInt(1, idCancion);
			psInsert.setInt(2, idPlaylist);
			return psInsert.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("No se pudo anadir cancion a la playlist: " + e.getMessage());
			return false;
		}
	}

	public void cerrarConexion() {
		closeConnection();
	}
	
	// Cerrar conexion
	public boolean closeConnection() {
		boolean connectionClosed = false;

		try {
			if (conect != null && !conect.isClosed()) {
				conect.close();
				connectionClosed = true;
			}
		} catch (SQLException e) {
			System.out.println("Error cerrando conexion: " + e.getMessage());
		}
		return connectionClosed;
	}

	/**
	 * Obtiene la conexión activa (para uso en otras clases)
	 */
	public Connection getConnection() {
		return this.conect;
	}

	/**
	 * Establece el id del cliente actual (útil cuando la autenticación se realiza fuera de sqlLogin)
	 */
	public void setIdClienteActual(int id) {
		this.idClienteActual = id;
	}

	public int getIdClienteActual() {
		return idClienteActual;
	}

	private Integer resolverIdioma(String idioma) {
			if (idioma == null || idioma.trim().isEmpty()) {
				return null;
			}

			String limpio = idioma.trim();
			try {
				return Integer.parseInt(limpio);
			} catch (NumberFormatException e) {
				String sql = "SELECT idIdioma FROM idioma WHERE descripcion = ?";
				try (PreparedStatement ps = conect.prepareStatement(sql)) {
					ps.setString(1, limpio);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							return rs.getInt("idIdioma");
						}
					}
				} catch (SQLException ex) {
					System.out.println("No se pudo resolver el idioma: " + ex.getMessage());
				}
			}
			return null;
		}

private boolean hayConexion() {
			try {
				return conect != null && !conect.isClosed();
			} catch (SQLException e) {
				return false;
			}
		}

		// Métodos adicionales basados en el ejemplo (consultas e inserciones)
		public ArrayList<Cliente> obtenerClientes() {
			ArrayList<Cliente> clientes = new ArrayList<Cliente>();
			String query = "SELECT c.idCliente, c.nombre, c.apellidos, i.descripcion, c.usuario, c.contrasena, c.fechaNacimiento, c.fechaRegistro, c.tipo "
					+ "FROM cliente c LEFT JOIN idioma i ON c.idIdioma = i.idIdioma";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					boolean esPremium = "Premium".equalsIgnoreCase(resultado.getString(9));
					Cliente nuevoCliente = new Cliente(resultado.getInt(1), resultado.getString(2), resultado.getString(3),
							resultado.getString(4), resultado.getString(5), resultado.getString(6), resultado.getString(7),
							resultado.getString(8), esPremium);
					clientes.add(nuevoCliente);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return clientes;
		}

		public ArrayList<Musico> obtenerMusicos() {
			ArrayList<Musico> musicos = new ArrayList<Musico>();
			String query = "SELECT a.idArtista,a.nombreArtistico,a.genero,a.descripcion,a.imagen,m.caracteristica"
					+ " FROM artista a join musico m on a.idArtista = m.idMusico";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					Musico nuevoMusico = new Musico(resultado.getInt(1), resultado.getString(2), resultado.getString(3),
							resultado.getString(4), resultado.getString(5), resultado.getString(6));
					musicos.add(nuevoMusico);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return musicos;
		}

		public ArrayList<Podcaster> obtenerPodcasters() {
			ArrayList<Podcaster> podcasters = new ArrayList<Podcaster>();
			String query = " SELECT a.idArtista,a.nombreArtistico,a.genero,a.descripcion,"
					+ "a.imagen FROM artista a join podcaster p on a.idArtista = p.idPodcaster";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					Podcaster nuevoPodcaster = new Podcaster(resultado.getInt(1), resultado.getString(2),
							resultado.getString(3), resultado.getString(4), resultado.getString(5));
					podcasters.add(nuevoPodcaster);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return podcasters;
		}

		public ArrayList<Album> obtenerAlbum(String nombreMusico) {
			ArrayList<Album> albums = new ArrayList<Album>();
			String query = "SELECT al.idAlbum, al.titulo, al.ano, al.genero, al.imagen, al.idMusico "
					+ "FROM album al WHERE al.idMusico IN (SELECT m.idMusico FROM musico m JOIN artista a ON m.idMusico = a.idArtista WHERE a.nombreArtistico = ?)";
			try (PreparedStatement consulta = conect.prepareStatement(query)) {
				consulta.setString(1, nombreMusico);
				try (ResultSet resultado = consulta.executeQuery()) {
				while (resultado.next()) {
					Album nuevoAlbum = new Album(resultado.getInt(1), resultado.getString(2), resultado.getString(3),
							resultado.getString(4), resultado.getString(5), resultado.getInt(6));
					albums.add(nuevoAlbum);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return albums;
		}

		public ArrayList<Cancion> obtenerCanciones(String nombreAlbum) {
			ArrayList<Cancion> canciones = new ArrayList<Cancion>();
			String query = "SELECT a.idAudio, a.nombre, a.archivo, a.duracion, a.nReproducciones, c.idAlbum, c.artistasInvitados "
					+ "FROM audio a JOIN cancion c ON c.idCancion = a.idAudio "
					+ "WHERE c.idAlbum = (SELECT idAlbum FROM album WHERE titulo = ? LIMIT 1)";
			try (PreparedStatement consulta = conect.prepareStatement(query)) {
				consulta.setString(1, nombreAlbum);
				try (ResultSet resultado = consulta.executeQuery()) {
				while (resultado.next()) {
					String tipo = "cancion";
					Time tiempo = resultado.getTime(4);
					int duracionSegundos = tiempo.toLocalTime().toSecondOfDay();
					Cancion nuevaCancion = new Cancion(resultado.getInt(1), resultado.getString(2), resultado.getString(3),
							duracionSegundos, resultado.getInt(5), resultado.getInt(6), resultado.getString(7), tipo);
					canciones.add(nuevaCancion);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return canciones;
		}

		public ArrayList<Podcast> obtenerPodcasts(String nomPodcaster) {
			ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
			String query = "SELECT a.idAudio, a.nombre, a.archivo, a.duracion, a.nReproducciones, p.colaboradores, p.idPodcaster "
					+ "FROM audio a JOIN podcast p ON p.idPodcast = a.idAudio "
					+ "WHERE p.idPodcaster = (SELECT a.idArtista FROM artista a JOIN podcaster p ON p.idPodcaster = a.idArtista WHERE a.nombreArtistico = ? LIMIT 1)";
			try (PreparedStatement consulta = conect.prepareStatement(query)) {
				consulta.setString(1, nomPodcaster);
				try (ResultSet resultado = consulta.executeQuery()) {
				while (resultado.next()) {
					String tipo = "podcast";
					Time tiempo = resultado.getTime("duracion");
					int duracionSegundos = tiempo.toLocalTime().toSecondOfDay();
					// colaboradores puede venir como texto o número; intentar parsear a int
					int numeroParticipantes = 0;
					try {
						String col = resultado.getString("colaboradores");
						if (col != null) {
							numeroParticipantes = Integer.parseInt(col);
						}
					} catch (Exception ex) {
						// no es un número; mantener 0
						numeroParticipantes = 0;
					}
					Podcast nuevoPodcast = new Podcast(resultado.getInt("idAudio"), resultado.getString("nombre"),
							resultado.getString("archivo"), duracionSegundos, resultado.getInt("nReproducciones"),
							resultado.getInt("idPodcaster"), numeroParticipantes, tipo);
					podcasts.add(nuevoPodcast);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return podcasts;
		}

		public ArrayList<Playlist> obtenerPlaylists(int idCliente) {
			ArrayList<Playlist> playlists = new ArrayList<Playlist>();
			String query = "SELECT * FROM playlist WHERE IdCliente = ?";
			try (PreparedStatement consulta = conect.prepareStatement(query)) {
				consulta.setInt(1, idCliente);
				try (ResultSet resultado = consulta.executeQuery()) {
				while (resultado.next()) {
					Playlist nuevaPlaylist = new Playlist(resultado.getInt(1), resultado.getString(2),
							resultado.getString(3), resultado.getInt(4));
					playlists.add(nuevaPlaylist);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return playlists;
		}

		public ArrayList<Cancion> obtenerCancionesPlaylist(int idPlaylist) {
			ArrayList<Cancion> cancionesPlaylist = new ArrayList<Cancion>();
			String query = "SELECT a.idAudio,a.nombre,a.archivo,a.duracion,a.nReproducciones,c.idAlbum ,"
					+ "c.artistasInvitados FROM `audio` a join cancion c on a.idAudio = c.idCancion "
					+ "join playlist_canciones p on p.idCancion = c.idCancion where p.idPlaylist = ?";
			try (PreparedStatement consulta = conect.prepareStatement(query)) {
				consulta.setInt(1, idPlaylist);
				try (ResultSet resultado = consulta.executeQuery()) {
				while (resultado.next()) {
					String tipo = "cancion";
					Time tiempo = resultado.getTime(4);
					int duracionSegundos = tiempo.toLocalTime().toSecondOfDay();
					Cancion nuevaCancion = new Cancion(resultado.getInt(1), resultado.getString(2), resultado.getString(3),
							duracionSegundos, resultado.getInt(5), resultado.getInt(6), resultado.getString(7), tipo);
					cancionesPlaylist.add(nuevaCancion);
				}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return cancionesPlaylist;
		}

		public void insertarMusico(Musico m) {
			try (Statement stmt = conect.createStatement()) {
				String queryArtista = "INSERT INTO artista (nombreArtistico, imagen, genero, descripcion) VALUES ('"
						+ m.getNombreArt() + "', '" + m.getFoto() + "', '" + m.getGenero() + "', '" + m.getDescripcion()
						+ "')";
				stmt.executeUpdate(queryArtista, Statement.RETURN_GENERATED_KEYS);
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					int idArtista = 0;
					if (rs.next()) {
						idArtista = rs.getInt(1);
					}
					String caracteristica = m.getComposicion();
					if (caracteristica == null || (!"Solista".equalsIgnoreCase(caracteristica) && !"Grupo".equalsIgnoreCase(caracteristica))) {
						caracteristica = "Solista";
					}
					String queryMusico = "INSERT INTO musico (idMusico, caracteristica) VALUES (" + idArtista + ", '"
							+ caracteristica + "')";
					stmt.executeUpdate(queryMusico);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void insertarPodcaster(Podcaster p) {
			try (Statement stmt = conect.createStatement()) {
				String queryArtista = "INSERT INTO artista (nombreArtistico, imagen, genero, descripcion) VALUES ('"
						+ p.getNombreArt() + "', '" + p.getFoto() + "', '" + p.getGenero() + "', '" + p.getDescripcion()
						+ "')";
				stmt.executeUpdate(queryArtista, Statement.RETURN_GENERATED_KEYS);
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					int idArtista = 0;
					if (rs.next()) {
						idArtista = rs.getInt(1);
					}
					String queryPodcaster = "INSERT INTO podcaster (idPodcaster) VALUES ('" + idArtista + "')";
					stmt.executeUpdate(queryPodcaster);
				}
			} catch (SQLException e) {
				if (e instanceof SQLIntegrityConstraintViolationException) {
					System.out.println("Artista ya existente: " + p.getNombreArt());
				} else {
					e.printStackTrace();
				}
			}
		}

		public void insertarCancion(Cancion c) {
			try (Statement stmt = conect.createStatement()) {
				int horas = c.getDuracionSegundos() / 3600;
				int minutos = (c.getDuracionSegundos() % 3600) / 60;
				int segundos = c.getDuracionSegundos() % 60;
				String duracionTime = String.format("%02d:%02d:%02d", horas, minutos, segundos);
				String queryAudio = "INSERT INTO audio (nombre, archivo, duracion, nReproducciones, tipo) VALUES ('"
					+ c.getNombreAudio() + "', '" + c.getArchivo() + "', '" + duracionTime + "', 0, 'Cancion')";
				stmt.executeUpdate(queryAudio, Statement.RETURN_GENERATED_KEYS);
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					int idAudio = 0;
					if (rs.next()) {
						idAudio = rs.getInt(1);
					}
					String queryCancion = "INSERT INTO cancion (idCancion, idAlbum, artistasInvitados) VALUES (" + idAudio
							+ ", " + c.getIdAlbum() + ", '" + c.getNombresColaboradores() + "')";
					stmt.executeUpdate(queryCancion);
				}
			} catch (SQLException e) {
				if (e instanceof SQLIntegrityConstraintViolationException) {
					System.out.println("Audio ya existente: " + c.getNombreAudio());
				} else {
					e.printStackTrace();
				}
			}
		}

		public void insertarAlbum(Album a) {
			try (Statement stmt = conect.createStatement()) {
				String anno = a.getFechaPub();
				if (anno == null) {
					anno = "";
				}
				anno = anno.trim();
				if (!anno.matches("\\d{4}")) {
					throw new SQLException("Año inválido: " + anno);
				}

				String query = "INSERT INTO album (titulo, ano, genero, imagen, idMusico) VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement ps = conect.prepareStatement(query)) {
					ps.setString(1, a.getTitulo());
					ps.setInt(2, Integer.parseInt(anno));
					ps.setString(3, a.getGenero());
					ps.setString(4, a.getFoto());
					ps.setInt(5, a.getIdMusico());
					ps.executeUpdate();
				}
			} catch (SQLException e) {
				if (e instanceof SQLIntegrityConstraintViolationException) {
					System.out.println("Álbum ya existente: " + a.getTitulo());
				} else {
					e.printStackTrace();
				}
			}
		}

		public void insertarPodcast(Podcast p) {
			try (Statement stmt = conect.createStatement()) {
				int horas = p.getDuracionSegundos() / 3600;
				int minutos = (p.getDuracionSegundos() % 3600) / 60;
				int segundos = p.getDuracionSegundos() % 60;
				String duracionTime = String.format("%02d:%02d:%02d", horas, minutos, segundos);
				String queryAudio = "INSERT INTO audio (nombre, archivo, duracion, nReproducciones, tipo) VALUES ('"
					+ p.getNombreAudio() + "', '" + p.getArchivo() + "', '" + duracionTime + "', 0, 'Podcast')";
				stmt.executeUpdate(queryAudio, Statement.RETURN_GENERATED_KEYS);
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					int idAudio = 0;
					if (rs.next()) {
						idAudio = rs.getInt(1);
					}
					String queryPodcast = "INSERT INTO podcast (idPodcast, colaboradores, idPodcaster) VALUES (" + idAudio
							+ ", " + p.getNumeroParticipantes() + ", " + p.getIdPodcaster() + ")";
					stmt.executeUpdate(queryPodcast);
				}
			} catch (SQLException e) {
				if (e instanceof SQLIntegrityConstraintViolationException) {
					System.out.println("Audio ya existente: " + p.getNombreAudio());
				} else {
					e.printStackTrace();
				}
			}
		}

		public boolean actualizarPodcaster(int idPodcaster, String nombre, String descripcion, String imagen) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "UPDATE artista SET nombreArtistico = ?, descripcion = ?, imagen = ? WHERE idArtista = (SELECT idPodcaster FROM podcaster WHERE idPodcaster = ?)";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, nombre);
				ps.setString(2, descripcion);
				ps.setString(3, imagen);
				ps.setInt(4, idPodcaster);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error actualizando podcaster: " + e.getMessage());
				return false;
			}
		}

		public boolean eliminarPodcaster(int idPodcaster) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM podcaster WHERE idPodcaster = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idPodcaster);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando podcaster: " + e.getMessage());
				return false;
			}
		}

		public boolean actualizarPodcast(int idPodcast, String nombre, String archivo, int duracion, int numeroParticipantes, String descripcion, int idPodcaster) {
			if (!hayConexion()) {
				return false;
			}

			int horas = duracion / 3600;
			int minutos = (duracion % 3600) / 60;
			int segundos = duracion % 60;
			String duracionTime = String.format("%02d:%02d:%02d", horas, minutos, segundos);

			String sql = "UPDATE audio SET nombre = ?, archivo = ?, duracion = ? WHERE idAudio = ? AND idAudio IN (SELECT idPodcast FROM podcast WHERE idPodcast = ?)";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, nombre);
				ps.setString(2, archivo);
				ps.setString(3, duracionTime);
				ps.setInt(4, idPodcast);
				ps.setInt(5, idPodcast);
				boolean result = ps.executeUpdate() > 0;

				if (result) {
					String sqlPodcast = "UPDATE podcast SET colaboradores = ? WHERE idPodcast = ?";
					try (PreparedStatement ps2 = conect.prepareStatement(sqlPodcast)) {
						ps2.setInt(1, numeroParticipantes);
						ps2.setInt(2, idPodcast);
						ps2.executeUpdate();
					}
				}
				return result;
			} catch (SQLException e) {
				System.out.println("Error actualizando podcast: " + e.getMessage());
				return false;
			}
		}

		public boolean eliminarPodcast(int idPodcast) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM podcast WHERE idPodcast = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idPodcast);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando podcast: " + e.getMessage());
				return false;
			}
		}

		public void insertarPlaylist(String titulo, int idCliente) {
			try (Statement stmt = conect.createStatement()) {
				String query = "INSERT INTO playlist ( titulo, fechaCreacion, idCliente) VALUES ('" + titulo
						+ "', CURRENT_DATE,'" + idCliente + "')";
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void insertarCancoinPlaylist(int idCancion, int idPlaylist) {
			try (Statement stmt = conect.createStatement()) {
				String query = "INSERT INTO playlist_canciones (idCancion, idPlaylist, fechaPlaylist_cancion) VALUES ("
						+ idCancion + ", " + idPlaylist + ", CURRENT_DATE)";
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void insertarCliente(Cliente c) {
			String tipo = c.isEsPremium() ? "Premium" : "Free";
			Integer idIdioma = resolverIdioma(c.getIdioma());
			String queryCliente = "INSERT INTO cliente (nombre, apellidos, usuario, contrasena, fechaNacimiento, fechaRegistro, tipo, idIdioma) VALUES (?, ?, ?, ?, ?, CURRENT_DATE, ?, ?)";
			try (PreparedStatement ps = conect.prepareStatement(queryCliente, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, c.getNombre());
				ps.setString(2, c.getApellido());
				ps.setString(3, c.getUsuario());
				ps.setString(4, c.getContrasena());
				ps.setString(5, c.getFecNac());
				ps.setString(6, tipo);
				if (idIdioma == null) {
					ps.setNull(7, java.sql.Types.INTEGER);
				} else {
					ps.setInt(7, idIdioma);
				}
				ps.executeUpdate();
				if (c.isEsPremium()) {
					try (ResultSet rs = ps.getGeneratedKeys()) {
						if (rs.next()) {
							int idCliente = rs.getInt(1);
							try (PreparedStatement psPremium = conect.prepareStatement("INSERT INTO premium (idCliente, fechaCaducidad) VALUES (?, CURRENT_DATE)")) {
								psPremium.setInt(1, idCliente);
								psPremium.executeUpdate();
							}
						}
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}




		public ArrayList<EstadisticaCancion> obtenerEstadisticasCanciones() {
			ArrayList<EstadisticaCancion> estadisticas = new ArrayList<>();
			String query = "Select * from cancionesmasescuchadas";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					EstadisticaCancion e = new EstadisticaCancion(resultado.getInt(1), resultado.getString(2), null,
						resultado.getInt(3));
					estadisticas.add(e);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return estadisticas;
		}

		public ArrayList<EstadisticaAudio> obtenerEstadisticasAudio() {
			ArrayList<EstadisticaAudio> estadisticas = new ArrayList<>();
			String query = "Select * from audiosmasescuchados";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					EstadisticaAudio e = new EstadisticaAudio(resultado.getInt(1), resultado.getString(2), resultado.getInt(4));
					estadisticas.add(e);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return estadisticas;
		}

		public ArrayList<EstadisticaPodcast> obtenerEstadisticasPodcast() {
			ArrayList<EstadisticaPodcast> estadisticas = new ArrayList<>();
			String query = "Select * from podcastmasescuchado";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					EstadisticaPodcast e = new EstadisticaPodcast(resultado.getInt(1), resultado.getString(2), resultado.getInt(3));
					estadisticas.add(e);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return estadisticas;
		}

		public ArrayList<EstadisticaPlaylist> obtenerEstadisticasPlaylist() {
			ArrayList<EstadisticaPlaylist> estadisticas = new ArrayList<>();
			String query = "Select * from playlistmasescuchada ";
			try (Statement consulta = conect.createStatement(); ResultSet resultado = consulta.executeQuery(query)) {
				while (resultado.next()) {
					EstadisticaPlaylist e = new EstadisticaPlaylist(resultado.getInt(1), resultado.getString(2), resultado.getInt(3));
					estadisticas.add(e);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return estadisticas;
		}

		// ============ MÉTODOS PARA VALIDACIÓN DE LÓGICA DE NEGOCIO ============

		/**
		 * Cuenta el número de playlists que tiene un usuario Free
		 */
		public int contarPlaylistsUsuario(int idCliente) {
			if (!hayConexion()) {
				return -1;
			}

			String sql = "SELECT COUNT(*) as total FROM playlist WHERE idCliente = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("total");
					}
				}
			} catch (SQLException e) {
				System.out.println("Error contando playlists: " + e.getMessage());
			}
			return -1;
		}

		/**
		 * Valida si un usuario puede crear una nueva playlist (máximo 3 para Free)
		 */
		public boolean puedeCrearPlaylist(int idCliente, boolean esPremium) {
			if (esPremium) {
				return true;
			}

			int playlistCount = contarPlaylistsUsuario(idCliente);
			return playlistCount < 3;
		}

		/**
		 * Registra la última reproducción de un audio por un usuario
		 */
		public boolean registrarUltimaReproduccion(int idCliente, int idAudio) {
			if (!hayConexion()) {
				return false;
			}

			String sqlCreateTable = "CREATE TABLE IF NOT EXISTS reproduccion_usuario (" +
					"idCliente INT, idAudio INT, ultimaReproduccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
					"PRIMARY KEY (idCliente, idAudio), " +
					"CONSTRAINT fk_repuser_cliente FOREIGN KEY (idCliente) REFERENCES cliente(idCliente) ON DELETE CASCADE, " +
					"CONSTRAINT fk_repuser_audio FOREIGN KEY (idAudio) REFERENCES audio(idAudio) ON DELETE CASCADE)";

			String sqlInsertUpdate = "INSERT INTO reproduccion_usuario (idCliente, idAudio, ultimaReproduccion) " +
					"VALUES (?, ?, CURRENT_TIMESTAMP) " +
					"ON DUPLICATE KEY UPDATE ultimaReproduccion = CURRENT_TIMESTAMP";

			try {
				try (Statement stmt = conect.createStatement()) {
					stmt.execute(sqlCreateTable);
				}

				try (PreparedStatement ps = conect.prepareStatement(sqlInsertUpdate)) {
					ps.setInt(1, idCliente);
					ps.setInt(2, idAudio);
					return ps.executeUpdate() > 0;
				}
			} catch (SQLException e) {
				System.out.println("Error registrando reproducción: " + e.getMessage());
			}
			return false;
		}

		/**
		 * Valida si un usuario Free puede reproducir una canción
		 * (debe esperar 10 minutos desde la última reproducción)
		 */
		public boolean puedeReproducirCancion(int idCliente, int idAudio, boolean esPremium) {
			if (esPremium) {
				return true;
			}

			if (!hayConexion()) {
				return false;
			}

			String sql = "SELECT ultimaReproduccion FROM reproduccion_usuario WHERE idCliente = ? AND idAudio = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				ps.setInt(2, idAudio);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						java.sql.Timestamp ultimaReproduccion = rs.getTimestamp("ultimaReproduccion");
						long tiempoTranscurrido = System.currentTimeMillis() - ultimaReproduccion.getTime();
						long diezMinutos = 10 * 60 * 1000; // 10 minutos en milisegundos
						return tiempoTranscurrido >= diezMinutos;
					}
					return true; // Primera vez reproduciendo
				}
			} catch (SQLException e) {
				System.out.println("Error validando reproducción: " + e.getMessage());
				return false;
			}
		}

		// ============ MÉTODOS CRUD PARA ADMINISTRADOR: ARTISTAS ============

		/**
		 * Obtiene un artista por su nombre
		 */
		public Artista obtenerArtistaPorNombre(String nombreArtistico) {
			if (!hayConexion()) {
				return null;
			}

			String sql = "SELECT idArtista, nombreArtistico, imagen, genero, descripcion FROM artista WHERE nombreArtistico = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, nombreArtistico);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return new Artista(rs.getInt("idArtista"), rs.getString("nombreArtistico"),
								rs.getString("genero"), rs.getString("descripcion"), rs.getString("imagen")) {
						};
					}
				}
			} catch (SQLException e) {
				System.out.println("Error obteniendo artista: " + e.getMessage());
			}
			return null;
		}

		/**
		 * Actualiza los datos de un artista
		 */
		public boolean actualizarArtista(int idArtista, String nombreArtistico, String genero, String descripcion, String imagen) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "UPDATE artista SET nombreArtistico = ?, genero = ?, descripcion = ?, imagen = ? WHERE idArtista = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, nombreArtistico);
				ps.setString(2, genero);
				ps.setString(3, descripcion);
				ps.setString(4, imagen);
				ps.setInt(5, idArtista);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error actualizando artista: " + e.getMessage());
				return false;
			}
		}

	/**
	 * Verifica si un artista ya existe por nombre
	 */
	public boolean existeArtista(String nombreArtistico) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM artista WHERE nombreArtistico = ?";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombreArtistico);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error verificando artista: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Verifica si un álbum ya existe por nombre
	 */
	public boolean existeAlbum(String nombreAlbum) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM album WHERE titulo = ?";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombreAlbum);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error verificando álbum: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Verifica si una canción ya existe por nombre
	 */
	public boolean existeCancion(String nombreCancion) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM audio WHERE nombre = ? AND tipo = 'Cancion'";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombreCancion);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error verificando canción: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Verifica si un podcaster ya existe por nombre
	 */
	public boolean existePodcaster(String nombrePodcaster) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM artista WHERE nombreArtistico = ? AND idArtista IN (SELECT idPodcaster FROM podcaster)";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombrePodcaster);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error verificando podcaster: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Verifica si un podcast ya existe por nombre
	 */
	public boolean existePodcast(String nombrePodcast) {
		if (!hayConexion()) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM audio WHERE nombre = ? AND tipo = 'Podcast'";
		try (PreparedStatement ps = conect.prepareStatement(sql)) {
			ps.setString(1, nombrePodcast);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error verificando podcast: " + e.getMessage());
		}
		return false;
	}

		/**
		 * Elimina un artista (y sus datos asociados en cascada)
		 */
		public boolean eliminarArtista(int idArtista) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM artista WHERE idArtista = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idArtista);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando artista: " + e.getMessage());
				return false;
			}
		}

		// ============ MÉTODOS CRUD PARA ADMINISTRADOR: ÁLBUMES ============

		/**
		 * Obtiene los detalles de un álbum por su título
		 */
		public Album obtenerAlbumPorTitulo(String titulo) {
			if (!hayConexion()) {
				return null;
			}

			String sql = "SELECT idAlbum, titulo, ano, genero, imagen, idMusico FROM album WHERE titulo = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, titulo);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return new Album(rs.getInt("idAlbum"), rs.getString("titulo"), rs.getString("ano"),
								rs.getString("genero"), rs.getString("imagen"), rs.getInt("idMusico"));
					}
				}
			} catch (SQLException e) {
				System.out.println("Error obteniendo álbum: " + e.getMessage());
			}
			return null;
		}

		/**
		 * Actualiza los datos de un álbum
		 */
		public boolean actualizarAlbum(int idAlbum, String titulo, String ano, String genero, String imagen,
				int idMusico) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "UPDATE album SET titulo = ?, ano = ?, genero = ?, imagen = ?, idMusico = ? WHERE idAlbum = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, titulo);
				ps.setString(2, ano);
				ps.setString(3, genero);
				ps.setString(4, imagen);
				ps.setInt(5, idMusico);
				ps.setInt(6, idAlbum);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error actualizando álbum: " + e.getMessage());
				return false;
			}
		}

		/**
		 * Elimina un álbum
		 */
		public boolean eliminarAlbum(int idAlbum) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM album WHERE idAlbum = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idAlbum);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando álbum: " + e.getMessage());
				return false;
			}
		}

		// ============ MÉTODOS CRUD PARA ADMINISTRADOR: CANCIONES ============

		/**
		 * Obtiene los detalles de una canción por su nombre
		 */
		public Cancion obtenerCancionPorNombre(String nombreCancion) {
			if (!hayConexion()) {
				return null;
			}

			String sql = "SELECT a.idAudio, a.nombre, a.archivo, a.duracion, a.nReproducciones, c.idAlbum, c.artistasInvitados " +
					"FROM audio a JOIN cancion c ON c.idCancion = a.idAudio WHERE a.nombre = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, nombreCancion);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						Time duracion = rs.getTime("duracion");
						int duracionSegundos = duracion != null ? duracion.toLocalTime().toSecondOfDay() : 0;
						return new Cancion(rs.getInt("idAudio"), rs.getString("nombre"), rs.getString("archivo"),
								duracionSegundos, rs.getInt("nReproducciones"), rs.getInt("idAlbum"),
								rs.getString("artistasInvitados"), "cancion");
					}
				}
			} catch (SQLException e) {
				System.out.println("Error obteniendo canción: " + e.getMessage());
			}
			return null;
		}

		/**
		 * Actualiza los datos de una canción
		 */
		public boolean actualizarCancion(int idCancion, String nombre, String archivo, String duracion, int idAlbum,
				String artistasInvitados) {
			if (!hayConexion()) {
				return false;
			}

			String sqlAudio = "UPDATE audio SET nombre = ?, archivo = ?, duracion = ? WHERE idAudio = ?";
			String sqlCancion = "UPDATE cancion SET idAlbum = ?, artistasInvitados = ? WHERE idCancion = ?";

			try (PreparedStatement psAudio = conect.prepareStatement(sqlAudio);
					PreparedStatement psCancion = conect.prepareStatement(sqlCancion)) {

				psAudio.setString(1, nombre);
				psAudio.setString(2, archivo);
				psAudio.setString(3, duracion);
				psAudio.setInt(4, idCancion);
				psAudio.executeUpdate();

				psCancion.setInt(1, idAlbum);
				psCancion.setString(2, artistasInvitados);
				psCancion.setInt(3, idCancion);
				return psCancion.executeUpdate() > 0;

			} catch (SQLException e) {
				System.out.println("Error actualizando canción: " + e.getMessage());
				return false;
			}
		}

		/**
		 * Elimina una canción
		 */
		public boolean eliminarCancion(int idCancion) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM cancion WHERE idCancion = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCancion);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando canción: " + e.getMessage());
				return false;
			}
		}

		/**
		 * Actualiza el cliente (tipo Premium/Free, datos personales)
		 */
		public boolean actualizarCliente(Cliente c) {
			if (!hayConexion()) {
				return false;
			}

			String tipo = c.isEsPremium() ? "Premium" : "Free";
			String sql = "UPDATE cliente SET nombre = ?, apellidos = ?, usuario = ?, contrasena = ?, tipo = ? WHERE idCliente = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setString(1, c.getNombre());
				ps.setString(2, c.getApellido());
				ps.setString(3, c.getUsuario());
				ps.setString(4, c.getContrasena());
				ps.setString(5, tipo);
				ps.setInt(6, c.getId());
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error actualizando cliente: " + e.getMessage());
				return false;
			}
		}

		// ============ MÉTODOS PARA GESTIÓN DE FAVORITOS ============

		/**
		 * Obtiene todos los audios favoritos de un cliente
		 */
		public ArrayList<Audio> obtenerFavoritos(int idCliente) {
			ArrayList<Audio> favoritos = new ArrayList<>();
			if (!hayConexion()) {
				return favoritos;
			}

			String sql = "SELECT a.idAudio, a.nombre, a.archivo, a.duracion, a.nReproducciones, a.tipo " +
					"FROM audio a JOIN favoritos f ON a.idAudio = f.idAudio WHERE f.idCliente = ? " +
					"ORDER BY a.nombre";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						Audio audio = crearAudioDesdeResultSet(rs);
						if (audio != null) {
							favoritos.add(audio);
						}
					}
				}
			} catch (SQLException e) {
				System.out.println("Error obteniendo favoritos: " + e.getMessage());
			}
			return favoritos;
		}

		/**
		 * Obtiene todos los audios de la base de datos.
		 */
		public ArrayList<Audio> obtenerAudios() {
			ArrayList<Audio> audios = new ArrayList<>();
			if (!hayConexion()) {
				return audios;
			}

			String sql = "SELECT a.idAudio, a.nombre, a.archivo, a.duracion, a.nReproducciones, a.tipo FROM audio a ORDER BY a.nombre";
			try (PreparedStatement ps = conect.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Audio audio = crearAudioDesdeResultSet(rs);
					if (audio != null) {
						audios.add(audio);
					}
				}
			} catch (SQLException e) {
				System.out.println("Error obteniendo audios: " + e.getMessage());
			}
			return audios;
		}

		/**
		 * Verifica si un audio está en favoritos de un cliente
		 */
		public boolean estaEnFavoritos(int idCliente, int idAudio) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "SELECT COUNT(*) as total FROM favoritos WHERE idCliente = ? AND idAudio = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				ps.setInt(2, idAudio);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("total") > 0;
					}
				}
			} catch (SQLException e) {
				System.out.println("Error verificando favorito: " + e.getMessage());
			}
			return false;
		}

		/**
		 * Agrega un audio a favoritos de un cliente
		 */
		public boolean agregarAFavoritos(int idCliente, int idAudio) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "INSERT INTO favoritos (idCliente, idAudio) VALUES (?, ?)";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				ps.setInt(2, idAudio);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				if (e instanceof SQLIntegrityConstraintViolationException) {
					// Ya existe en favoritos
					return false;
				}
				System.out.println("Error agregando a favoritos: " + e.getMessage());
				return false;
			}
		}

		/**
		 * Elimina un audio de favoritos de un cliente
		 */
		public boolean eliminarDeFavoritos(int idCliente, int idAudio) {
			if (!hayConexion()) {
				return false;
			}

			String sql = "DELETE FROM favoritos WHERE idCliente = ? AND idAudio = ?";
			try (PreparedStatement ps = conect.prepareStatement(sql)) {
				ps.setInt(1, idCliente);
				ps.setInt(2, idAudio);
				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				System.out.println("Error eliminando de favoritos: " + e.getMessage());
				return false;
			}
		}

		/**
		 * Helper: Crear un objeto Audio desde un ResultSet
		 */
		private Audio crearAudioDesdeResultSet(ResultSet rs) throws SQLException {
			try {
				int id = rs.getInt("idAudio");
				String nombre = rs.getString("nombre");
				String archivo = rs.getString("archivo");
				int duracion = rs.getInt("duracion");
				int nRep = rs.getInt("nReproducciones");
				String tipo = rs.getString("tipo");

				if ("Cancion".equalsIgnoreCase(tipo)) {
					return new Cancion(id, nombre, archivo, duracion, nRep, 0, null, tipo);
				} else if ("Podcast".equalsIgnoreCase(tipo)) {
					return new Podcast(id, nombre, archivo, duracion, nRep, 0, 0, tipo);
				}
			} catch (Exception e) {
				System.out.println("Error creando audio: " + e.getMessage());
			}
			return null;
		}

}
