package modelo;

/**
 * Representa estadísticas agregadas de un podcast.
 */
public class EstadisticaPodcast {
    private int idPodcast;
    private String nombre;
    private int reproducciones;

    /** Crea una estadística vacía. */
    public EstadisticaPodcast() {}

    /**
     * Crea una estadística de podcast.
     *
     * @param idPodcast id del podcast
     * @param nombre nombre del podcast
     * @param reproducciones número de reproducciones
     */
    public EstadisticaPodcast(int idPodcast, String nombre, int reproducciones) {
        this.idPodcast = idPodcast;
        this.nombre = nombre;
        this.reproducciones = reproducciones;
    }

    /** Obtiene el id del podcast. */
    public int getIdPodcast() { return idPodcast; }
    /** Establece el id del podcast. */
    public void setIdPodcast(int idPodcast) { this.idPodcast = idPodcast; }
    /** Obtiene el nombre del podcast. */
    public String getNombre() { return nombre; }
    /** Establece el nombre del podcast. */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** Obtiene el número de reproducciones. */
    public int getReproducciones() { return reproducciones; }
    /** Establece el número de reproducciones. */
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaPodcast [idPodcast=" + idPodcast + ", nombre=" + nombre + ", reproducciones=" + reproducciones + "]";
    }
}
