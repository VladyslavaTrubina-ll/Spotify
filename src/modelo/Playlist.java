package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una playlist de un cliente con sus canciones asociadas.
 */
public class Playlist {
	private int id, idCliente;
	private String titulo, fechaCreacion;
	private List<Cancion> canciones;

	/**
	 * Crea una playlist vacía.
	 */
	public Playlist() {
		this.canciones = new ArrayList<>();
	}

	/**
	 * Crea una playlist con todos sus metadatos.
	 *
	 * @param id identificador de la playlist
	 * @param titulo título de la playlist
	 * @param fechaCreacion fecha de creación en formato texto
	 * @param idCliente id del cliente propietario
	 */
	public Playlist(int id, String titulo, String fechaCreacion, int idCliente) {
		this.id = id;
		this.titulo = titulo;
		this.idCliente = idCliente;
		this.fechaCreacion = fechaCreacion;
		this.canciones = new ArrayList<>();
	}

	/**
	 * Constructor con metadatos.
	 *
	 * @param id identificador de la playlist
	 * @param titulo título de la playlist
	 * @param fechaCreacion fecha de creación en formato texto
	 * @param idCliente id del cliente propietario
	 */

	@Override
	public String toString() {
		return "Playlist [id=" + id + ", idCliente=" + idCliente + ", titulo=" + titulo + ", fechaCreacion="
				+ fechaCreacion + ", canciones=" + canciones.size() + "]";
	}

	/**
	 * Obtiene el identificador de la playlist.
	 *
	 * @return id de la playlist
	 */
	public int getId() {
		return id;
	}

	/**
	 * Establece el identificador de la playlist.
	 *
	 * @param id nuevo id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Obtiene el id del cliente propietario.
	 *
	 * @return id del cliente
	 */
	public int getIdCliente() {
		return idCliente;
	}

	/**
	 * Establece el id del cliente propietario.
	 *
	 * @param idCliente id del cliente
	 */
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	/**
	 * Obtiene el título de la playlist.
	 *
	 * @return título de la playlist
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Establece el título de la playlist.
	 *
	 * @param titulo nuevo título
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Obtiene la fecha de creación en formato texto.
	 *
	 * @return fecha de creación
	 */
	public String getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * Establece la fecha de creación.
	 *
	 * @param fechaCreacion fecha de creación
	 */
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * Obtiene la lista de canciones de la playlist.
	 *
	 * @return lista de canciones
	 */
	public List<Cancion> getCanciones() {
		return canciones;
	}

	/**
	 * Reemplaza la lista de canciones.
	 *
	 * @param canciones nueva lista; si es null se crea una vacía
	 */
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones != null ? canciones : new ArrayList<>();
	}

	/**
	 * Añade una canción si no está ya presente.
	 *
	 * @param c canción a añadir
	 * @return true si fue añadida
	 */
	public boolean addCancion(Cancion c) {
		if (c == null) return false;
		for (Cancion existing : canciones) {
			if (existing.getId() == c.getId()) return false;
		}
		canciones.add(c);
		return true;
	}

	/**
	 * Elimina una canción por su identificador.
	 *
	 * @param idCancion id de la canción
	 * @return true si se eliminó alguna canción
	 */
	public boolean removeCancionById(int idCancion) {
		return canciones.removeIf(c -> c.getId() == idCancion);
	}

	/**
	 * Calcula la duración total de la playlist en segundos.
	 *
	 * @return suma de duraciones de todas las canciones
	 */
	public int obtenerDuracionTotalSegundos() {
		int total = 0;
		for (Cancion c : canciones) {
			if (c != null) total += c.getDuracionSegundos();
		}
		return total;
	}

	/**
	 * Comprueba si existe una canción por id en la playlist.
	 *
	 * @param idCancion id de la canción
	 * @return true si la canción está incluida
	 */
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
