package modelo;

import java.util.*;

public class Album {
	private int id;
	private String titulo;
	private String anno;
	private String genero;
	private String imagen;
	private int idMusico;

	public Album() {}

	public Album(int id, String titulo, String anno, String genero, String imagen, int idMusico) {
		this.id = id;
		this.titulo = titulo;
		this.anno = anno;
		this.genero = genero;
		this.imagen = imagen;
		this.idMusico = idMusico;
	}

	public int getId() { return id; }
	public String getTitulo() { return titulo; }
	public String getFechaPub() { return anno; }
	public String getAnno() { return anno; }
	public String getGenero() { return genero; }
	public String getFoto() { return imagen; }
	public String getImagen() { return imagen; }
	public int getIdMusico() { return idMusico; }

	@Override
	public String toString() {
		return "Album[id="+id+", titulo="+titulo+", anno="+anno+", genero="+genero+"]";
	}
}
