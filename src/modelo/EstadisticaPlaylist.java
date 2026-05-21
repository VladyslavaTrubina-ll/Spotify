package modelo;

/**
 * Representa estadísticas agregadas de una playlist.
 */
public class EstadisticaPlaylist {
    private int idPlaylist;
    private String nombrePlaylist;
    private int reproducciones;

    /** Crea una estadística vacía. */
    public EstadisticaPlaylist() {}

    /**
     * Crea una estadística de playlist.
     *
     * @param idPlaylist id de la playlist
     * @param nombrePlaylist nombre de la playlist
     * @param reproducciones número de reproducciones
     */
    public EstadisticaPlaylist(int idPlaylist, String nombrePlaylist, int reproducciones) {
        this.idPlaylist = idPlaylist;
        this.nombrePlaylist = nombrePlaylist;
        this.reproducciones = reproducciones;
    }

    /** Obtiene el id de la playlist. */
    public int getIdPlaylist() { return idPlaylist; }
    /** Establece el id de la playlist. */
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }
    /** Obtiene el nombre de la playlist. */
    public String getNombrePlaylist() { return nombrePlaylist; }
    /** Establece el nombre de la playlist. */
    public void setNombrePlaylist(String nombrePlaylist) { this.nombrePlaylist = nombrePlaylist; }
    /** Obtiene el número de reproducciones. */
    public int getReproducciones() { return reproducciones; }
    /** Establece el número de reproducciones. */
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaPlaylist [idPlaylist=" + idPlaylist + ", nombrePlaylist=" + nombrePlaylist + ", reproducciones=" + reproducciones + "]";
    }
}
