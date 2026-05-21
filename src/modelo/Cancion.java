package modelo;

import java.util.*;

/**
 * Clase que representa una canción con su información de álbum y colaboradores.
 */
public class Cancion extends Audio {
	private int idAlbum;
	private String nombresColaboradores;
	private String foto;

	public Cancion(int id, String nombreAudio, String archivo, int duracionSegundos, int reproducciones, int idAlbum, String nombresColaboradores, String tipo) {
		super(id, nombreAudio, archivo, reproducciones, duracionSegundos, tipo);
		this.idAlbum = idAlbum;
		this.nombresColaboradores = nombresColaboradores;
		this.foto = null;
	}

	public Cancion(int id, String nombreAudio, String archivo, int duracionSegundos, int reproducciones, int idAlbum,
			String nombresColaboradores, String tipo, String foto) {
		super(id, nombreAudio, archivo, reproducciones, duracionSegundos, tipo);
		this.idAlbum = idAlbum;
		this.nombresColaboradores = nombresColaboradores;
		this.foto = foto;
	}

	public int getIdAlbum() {
		return idAlbum;
	}

	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}

	public String getNombresColaboradores() {
		return nombresColaboradores;
	}

	public void setNombresColaboradores(String nombresColaboradores) {
		this.nombresColaboradores = nombresColaboradores;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Override
	public String toString() {
		return "Cancion[" + super.toString() + ", colaboradores=" + nombresColaboradores + ", foto=" + foto + "]";
	}
}
