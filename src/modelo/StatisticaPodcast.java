package modelo;

public class StatisticaPodcast {
    private int idPodcast;
    private int numReproduciones;
    private String nombreArtistico;
    private String nombreAudio;

    public StatisticaPodcast(int idPodcast , String nombreArtistico, String nombreAudio, int numReproduciones) {
        this.idPodcast = idPodcast;
        this.nombreArtistico = nombreArtistico;
        this.nombreAudio = nombreAudio;
        this.numReproduciones = numReproduciones;
    }

    @Override
    public String toString() {
        return "StatisticaPodcast [idPodcast=" + idPodcast + ", numReproduciones=" + numReproduciones
                + ", nombreArtistico=" + nombreArtistico + ", nombreAudio=" + nombreAudio + "]";
    }

    public int getIdPodcast() { return idPodcast; }
    public void setIdPodcast(int idPodcast) { this.idPodcast = idPodcast; }
    public int getNumReproduciones() { return numReproduciones; }
    public void setNumReproduciones(int numReproduciones) { this.numReproduciones = numReproduciones; }
    public String getNombreArtistico() { return nombreArtistico; }
    public void setNombreArtistico(String nombreArtistico) { this.nombreArtistico = nombreArtistico; }
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }

    // Alias getters for compatibility with previous naming
    public int getId() { return idPodcast; }
    public String getNombre() { return nombreAudio; }
    public String getPodcaster() { return nombreArtistico; }
    public int getReproducciones() { return numReproduciones; }
}
