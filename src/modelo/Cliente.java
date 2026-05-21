package modelo;

import java.util.*;

public class Cliente {
	private int id, limitesPlaylists;
	private String nombre, apellido, idioma, usuario, contrasena, fecNac, fecReg;
	private boolean esPremium;
	private ArrayList<Playlist> playlistCliente;

	/**
	 * Crea un cliente vacío.
	 */
	public Cliente() {

	}

	/**
	 * Crea un cliente con sus datos principales.
	 *
	 * @param id identificador
	 * @param nombre nombre
	 * @param apellido apellido
	 * @param idioma idioma
	 * @param usuario usuario
	 * @param contrasena contraseña
	 * @param fecNac fecha de nacimiento
	 * @param fecReg fecha de registro
	 * @param esPremium indica si la cuenta es premium
	 */
	public Cliente(int id, String nombre, String apellido, String idioma, String usuario, String contrasena,
			String fecNac, String fecReg, boolean esPremium) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.idioma = idioma;
		this.usuario = usuario;
		this.contrasena = contrasena;
		this.fecNac = fecNac;
		this.fecReg = fecReg;
		this.esPremium = esPremium;
		this.playlistCliente = new ArrayList<Playlist>();
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", limitesPlaylists=" + limitesPlaylists + ", nombre=" + nombre + ", apellido="
				+ apellido + ", idioma=" + idioma + ", usuario=" + usuario + ", contrasena=" + contrasena + ", fecNac="
				+ fecNac + ", fecReg=" + fecReg + ", esPremium=" + esPremium + ", playlistCliente=" + playlistCliente
				+ "]";
	}

	/** Obtiene el identificador del cliente. */
	public int getId() {
		return id;
	}

	/** Establece el identificador del cliente. */

	public void setId(int id) {
		this.id = id;
	}

	/** Obtiene el límite de playlists. */
	public int getLimitesPlaylists() {
		return limitesPlaylists;
	}

	/** Establece el límite de playlists. */

	public void setLimitesPlaylists(int limitesPlaylists) {
		this.limitesPlaylists = limitesPlaylists;
	}

	/** Obtiene el nombre. */
	public String getNombre() {
		return nombre;
	}

	/** Establece el nombre. */

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/** Obtiene el apellido. */
	public String getApellido() {
		return apellido;
	}

	/** Establece el apellido. */

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/** Obtiene el idioma. */
	public String getIdioma() {
		return idioma;
	}

	/** Establece el idioma. */

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	/** Obtiene el usuario. */
	public String getUsuario() {
		return usuario;
	}

	/** Establece el usuario. */

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/** Obtiene la contraseña. */
	public String getContrasena() {
		return contrasena;
	}

	/** Establece la contraseña. */

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	/** Obtiene la fecha de nacimiento. */
	public String getFecNac() {
		return fecNac;
	}

	/** Establece la fecha de nacimiento. */

	public void setFecNac(String fecNac) {
		this.fecNac = fecNac;
	}

	/** Obtiene la fecha de registro. */
	public String getFecReg() {
		return fecReg;
	}

	/** Establece la fecha de registro. */

	public void setFecReg(String fecReg) {
		this.fecReg = fecReg;
	}

	/** Indica si la cuenta es premium. */
	public boolean isEsPremium() {
		return esPremium;
	}

	/** Establece el estado premium. */

	public void setEsPremium(boolean esPremium) {
		this.esPremium = esPremium;
		if (!esPremium) {
			this.limitesPlaylists = 3;
		} else {
			this.limitesPlaylists = Integer.MAX_VALUE;
		}
	}

	/** Obtiene la lista de playlists del cliente. */
	public ArrayList<Playlist> getPlaylistCliente() {
		return playlistCliente;
	}

	/** Establece la lista de playlists del cliente. */

	public void setPlaylistCliente(ArrayList<Playlist> playlistCliente) {
		this.playlistCliente = playlistCliente;
	}

}
