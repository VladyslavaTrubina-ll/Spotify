package modelo;

import java.util.*;

public class Playlist {
	private int id, idCliente;
	private String titulo, fechaCreacion;
	private List<Cancion> canciones;

	public Playlist() {
		this.canciones = new ArrayList<>();
	}

	public Playlist(int id, String titulo, String fechaCreacion, int idCliente) {
		this.id = id;
		this.titulo = titulo;
		this.idCliente = idCliente;
		this.fechaCreacion = fechaCreacion;
		this.canciones = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "Playlist [id=" + id + ", idCliente=" + idCliente + ", titulo=" + titulo + ", fechaCreacion="
				+ fechaCreacion + ", canciones=" + canciones.size() + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public List<Cancion> getCanciones() {
		return canciones;
	}

	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones != null ? canciones : new ArrayList<>();
	}

	// Utility: add a song if not already present
	public boolean addCancion(Cancion c) {
		if (c == null) return false;
		for (Cancion existing : canciones) {
			if (existing.getId() == c.getId()) return false;
		}
		canciones.add(c);
		return true;
	}

	// Remove by audio/song id
	public boolean removeCancionById(int idCancion) {
		return canciones.removeIf(c -> c.getId() == idCancion);
	}

	// Total duration in seconds (safely handles nulls)
	public int obtenerDuracionTotalSegundos() {
		int total = 0;
		for (Cancion c : canciones) {
			if (c != null) total += c.getDuracionSegundos();
		}
		return total;
	}

	// Check if playlist contains song id
	public boolean containsCancion(int idCancion) {
		for (Cancion c : canciones) if (c != null && c.getId() == idCancion) return true;
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Playlist)) return false;
		Playlist p = (Playlist) o;
		return id == p.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
