package modelo;

/**
 * Representa estadísticas agregadas de un audio.
 */
public class EstadisticaAudio {
    private int idAudio;
    private String nombre;
    private int reproducciones;

    /** Crea una estadística vacía. */
    public EstadisticaAudio() {}

    /**
     * Crea una estadística de audio.
     *
     * @param idAudio id del audio
     * @param nombre nombre del audio
     * @param reproducciones número de reproducciones
     */
    public EstadisticaAudio(int idAudio, String nombre, int reproducciones) {
        this.nombre = nombre;
        this.reproducciones = reproducciones;
        this.idAudio = idAudio;
    }

    /** Obtiene el id del audio. */
    public int getIdAudio() { return idAudio; }
    /** Establece el id del audio. */
    public void setIdAudio(int idAudio) { this.idAudio = idAudio; }
    /** Obtiene el nombre del audio. */
    public String getNombre() { return nombre; }
    /** Establece el nombre del audio. */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** Obtiene el número de reproducciones. */
    public int getReproducciones() { return reproducciones; }
    /** Establece el número de reproducciones. */
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaAudio [idAudio=" + idAudio + ", nombre=" + nombre + ", reproducciones=" + reproducciones + "]";
    }
}
