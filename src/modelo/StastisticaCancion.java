package modelo;

/**
 * Clase de compatibilidad para estadísticas de canción con nomenclatura antigua.
 */
public class StastisticaCancion {
    private int idCancion;
    private int Nreproduciones;
    private String nombreAudio;
    private String nombreArtista;

    /** Crea una estadística vacía. */
    public StastisticaCancion() {
    }

    /**
     * Crea una estadística de canción (nomenclatura antigua).
     *
     * @param idCancion id de la canción
     * @param nombreAudio nombre del audio
     * @param nombreArtista nombre del artista
     * @param Nreproduciones número de reproducciones
     */
    public StastisticaCancion(int idCancion, String nombreAudio, String nombreArtista, int Nreproduciones) {
        this.idCancion = idCancion;
        this.nombreAudio = nombreAudio;
        this.nombreArtista = nombreArtista;
        this.Nreproduciones = Nreproduciones;
    }

    @Override
    public String toString() {
        return "StastisticaCancion [idCancion=" + idCancion + ", Nreproduciones=" + Nreproduciones + ", nombreAudio="
                + nombreAudio + ", nombreArtista=" + nombreArtista + "]";
    }

    /** Obtiene el id de la canción. */
    public int getIdCancion() { return idCancion; }
    /** Establece el id de la canción. */
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }
    /** Obtiene el número de reproducciones. */
    public int getNreproduciones() { return Nreproduciones; }
    /** Establece el número de reproducciones. */
    public void setNreproduciones(int nreproduciones) { Nreproduciones = nreproduciones; }
    /** Obtiene el nombre del audio. */
    public String getNombreAudio() { return nombreAudio; }
    /** Establece el nombre del audio. */
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    /** Obtiene el nombre del artista. */
    public String getNombreArtista() { return nombreArtista; }
    /** Establece el nombre del artista. */
    public void setNombreArtista(String nombreArtista) { this.nombreArtista = nombreArtista; }

    // Alias getters for compatibility with previous naming
    public int getId() { return idCancion; }
    public String getNombre() { return nombreAudio; }
    public String getAlbum() { return nombreArtista; }
    public int getReproducciones() { return Nreproduciones; }
}
