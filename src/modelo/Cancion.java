package modelo;

import java.util.*;

public class Cancion extends Audio {
	private int idAlbum;
	private String nombresColaboradores;

	public Cancion(int id, String nombreAudio, String archivo, int duratasecondi, int NumRep, int idAlbum, String nombresColaboradores, String tipo) {
		super(id, nombreAudio, archivo, NumRep, duratasecondi, tipo);
		this.idAlbum = idAlbum;
		this.nombresColaboradores = nombresColaboradores;
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

	@Override
	public String toString() {
		return "Cancion[" + super.toString() + ", colaboradores=" + nombresColaboradores + "]";
	}
}
