package controlador;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
		boolean connectionEstabli = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conect = DriverManager.getConnection("jdbc:mysql://localhost/" + this.nombreDB, "root", "");
			connectionEstabli = true;
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontró la librería de sql");
		} catch (SQLException e) {
			System.out.println("No se pudo conectar a la BD " + this.nombreDB);
		}
		return connectionEstabli;
	}

	public boolean iniciarSesion(String alias, String contrasenaUsuario) {
		return sqlLogin(alias, contrasenaUsuario);
	}

	public boolean sqlLogin(String nombreUsuario, String contrasenaUsuario) {
		if (!hayConexionActiva()) {
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
		if (!hayConexionActiva()) {
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
		if (!hayConexionActiva()) {
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
		if (!hayConexionActiva()) {
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
		if (!hayConexionActiva() || idClienteActual <= 0) {
			return false;
		}

		String sql = "INSERT INTO playlist (titulo, fechaCreacion, IdCliente) VALUES (?, CURRENT_DATE, ?)";
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
		if (!hayConexionActiva() || idClienteActual <= 0) {
			return false;
		}

		String sql = "DELETE FROM playlist WHERE titulo = ? AND IdCliente = ?";
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
		if (!hayConexionActiva() || idClienteActual <= 0) {
			return false;
		}

		String sqlPlaylist = "SELECT IDlist FROM playlist WHERE titulo = ? AND IdCliente = ?";
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
					idPlaylist = rsPlaylist.getInt("IDlist");
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
				System.out.println("No hay conexion con la BD");
			}
			return connectionClosed;
		}

		private Integer resolverIdioma(String idioma) {
			if (idioma == null || idioma.trim().isEmpty()) {
				return null;
			}

			String limpio = idioma.trim();
			try {
				return Integer.parseInt(limpio);
			} catch (NumberFormatException e) {
				String sql = "SELECT idIdioma FROM idioma WHERE description = ?";
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

		private boolean hayConexionActiva() {
			try {
				return conect != null && !conect.isClosed();
			} catch (SQLException e) {
				return false;
			}
		}

}
