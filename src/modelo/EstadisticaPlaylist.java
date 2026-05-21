package modelo;

public class EstadisticaPlaylist {
    private int idPlaylist;
    private String nombrePlaylist;
    private int reproducciones;

    public EstadisticaPlaylist() {}

    public EstadisticaPlaylist(int idPlaylist, String nombrePlaylist, int reproducciones) {
        this.idPlaylist = idPlaylist;
        this.nombrePlaylist = nombrePlaylist;
        this.reproducciones = reproducciones;
    }

    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }
    public String getNombrePlaylist() { return nombrePlaylist; }
    public void setNombrePlaylist(String nombrePlaylist) { this.nombrePlaylist = nombrePlaylist; }
    public int getReproducciones() { return reproducciones; }
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaPlaylist [idPlaylist=" + idPlaylist + ", nombrePlaylist=" + nombrePlaylist + ", reproducciones=" + reproducciones + "]";
    }
}
