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
		/**
		 * Constructor principal de Cancion.
		 */
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

	/** Obtiene el id del álbum al que pertenece la canción. */
	public int getIdAlbum() {
		return idAlbum;
	}

	/** Establece el id del álbum. */
	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}

	/** Obtiene los nombres de colaboradores (texto). */
	public String getNombresColaboradores() {
		return nombresColaboradores;
	}

	/** Establece los nombres de colaboradores. */
	public void setNombresColaboradores(String nombresColaboradores) {
		this.nombresColaboradores = nombresColaboradores;
	}

	/** Obtiene la ruta/identificador de la imagen asociada a la canción. */
	public String getFoto() {
		return foto;
	}

	/** Establece la foto asociada a la canción. */
	public void setFoto(String foto) {
		this.foto = foto;
	}

	@Override
	public String toString() {
		return "Cancion[" + super.toString() + ", colaboradores=" + nombresColaboradores + ", foto=" + foto + "]";
	}
}
