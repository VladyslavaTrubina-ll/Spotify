package modelo;

public class EstadisticaCancion {
    private int idCancion;
    private int reproducciones;
    private String nombreAudio;
    private String nombreArtista;

    public EstadisticaCancion() {}

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

    public int getIdCancion() { return idCancion; }
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }
    public int getReproducciones() { return reproducciones; }
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    public String getNombreArtista() { return nombreArtista; }
    public void setNombreArtista(String nombreArtista) { this.nombreArtista = nombreArtista; }

    // Convenience aliases
    public int getId() { return idCancion; }
    public String getNombre() { return nombreAudio; }
}
