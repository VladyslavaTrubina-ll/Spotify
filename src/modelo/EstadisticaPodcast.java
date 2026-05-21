package modelo;

public class EstadisticaPodcast {
    private int idPodcast;
    private String nombre;
    private int reproducciones;

    public EstadisticaPodcast() {}

    public EstadisticaPodcast(int idPodcast, String nombre, int reproducciones) {
        this.idPodcast = idPodcast;
        this.nombre = nombre;
        this.reproducciones = reproducciones;
    }

    public int getIdPodcast() { return idPodcast; }
    public void setIdPodcast(int idPodcast) { this.idPodcast = idPodcast; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getReproducciones() { return reproducciones; }
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaPodcast [idPodcast=" + idPodcast + ", nombre=" + nombre + ", reproducciones=" + reproducciones + "]";
    }
}
