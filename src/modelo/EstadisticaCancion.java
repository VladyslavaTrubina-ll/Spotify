package modelo;

/**
 * Representa estadísticas agregadas de una canción.
 */
public class EstadisticaCancion {
    private int idCancion;
    private int reproducciones;
    private String nombreAudio;
    private String nombreArtista;

    /** Crea una estadística vacía. */
    public EstadisticaCancion() {}

    /**
     * Crea una estadística de canción.
     *
     * @param idCancion id de la canción
     * @param nombreAudio nombre del audio
     * @param nombreArtista nombre del artista
     * @param reproducciones número de reproducciones
     */
    public EstadisticaCancion(int idCancion, String nombreAudio, String nombreArtista, int reproducciones) {
        this.idCancion = idCancion;
        this.nombreAudio = nombreAudio;
        this.nombreArtista = nombreArtista;
        this.reproducciones = reproducciones;
    }

    @Override
    public String toString() {
        return "EstadisticaCancion [idCancion=" + idCancion + ", reproducciones=" + reproducciones + ", nombreAudio="
                + nombreAudio + ", nombreArtista=" + nombreArtista + "]";
    }

    /** Obtiene el id de la canción. */
    public int getIdCancion() { return idCancion; }
    /** Establece el id de la canción. */
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }
    /** Obtiene el número de reproducciones. */
    public int getReproducciones() { return reproducciones; }
    /** Establece el número de reproducciones. */
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }
    /** Obtiene el nombre del audio. */
    public String getNombreAudio() { return nombreAudio; }
    /** Establece el nombre del audio. */
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    /** Obtiene el nombre del artista. */
    public String getNombreArtista() { return nombreArtista; }
    /** Establece el nombre del artista. */
    public void setNombreArtista(String nombreArtista) { this.nombreArtista = nombreArtista; }

    // Convenience aliases
    public int getId() { return idCancion; }
    public String getNombre() { return nombreAudio; }
}
