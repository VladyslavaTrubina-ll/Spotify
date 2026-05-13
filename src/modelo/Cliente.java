package modelo;

import java.util.*;

public class Cliente {
	private int id, limitesPlaylists;
	private String nombre, apellido, idioma, usuario, contrasena, fecNac, fecReg;
	private boolean esPremium;
	private ArrayList<Playlist> playlistCliente;

	public Cliente() {

	}

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLimitesPlaylists() {
		return limitesPlaylists;
	}

	public void setLimitesPlaylists(int limitesPlaylists) {
		this.limitesPlaylists = limitesPlaylists;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getFecNac() {
		return fecNac;
	}

	public void setFecNac(String fecNac) {
		this.fecNac = fecNac;
	}

	public String getFecReg() {
		return fecReg;
	}

	public void setFecReg(String fecReg) {
		this.fecReg = fecReg;
	}

	public boolean isEsPremium() {
		return esPremium;
	}

	public void setEsPremium(boolean esPremium) {
		this.esPremium = esPremium;
		if (!esPremium) {
			this.limitesPlaylists = 3;
		} else {
			this.limitesPlaylists = Integer.MAX_VALUE;
		}
	}

	public ArrayList<Playlist> getPlaylistCliente() {
		return playlistCliente;
	}

	public void setPlaylistCliente(ArrayList<Playlist> playlistCliente) {
		this.playlistCliente = playlistCliente;
	}

}
