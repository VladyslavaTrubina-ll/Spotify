package modelo;

public class StastisticaCancion {
    private int idCancion;
    private int Nreproduciones;
    private String nombreAudio;
    private String nombreArtista;

    public StastisticaCancion() {
    }

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

    public int getIdCancion() { return idCancion; }
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }
    public int getNreproduciones() { return Nreproduciones; }
    public void setNreproduciones(int nreproduciones) { Nreproduciones = nreproduciones; }
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    public String getNombreArtista() { return nombreArtista; }
    public void setNombreArtista(String nombreArtista) { this.nombreArtista = nombreArtista; }

    // Alias getters for compatibility with previous naming
    public int getId() { return idCancion; }
    public String getNombre() { return nombreAudio; }
    public String getAlbum() { return nombreArtista; }
    public int getReproducciones() { return Nreproduciones; }
}
