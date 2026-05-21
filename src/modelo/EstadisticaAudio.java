package modelo;

public class EstadisticaAudio {
    private int idAudio;
    private String nombre;
    private int reproducciones;

    public EstadisticaAudio() {}

    public EstadisticaAudio(int idAudio, String nombre, int reproducciones) {
        this.idAudio = idAudio;
        this.nombre = nombre;
        this.reproducciones = reproducciones;
    }

    public int getIdAudio() { return idAudio; }
    public void setIdAudio(int idAudio) { this.idAudio = idAudio; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getReproducciones() { return reproducciones; }
    public void setReproducciones(int reproducciones) { this.reproducciones = reproducciones; }

    @Override
    public String toString() {
        return "EstadisticaAudio [idAudio=" + idAudio + ", nombre=" + nombre + ", reproducciones=" + reproducciones + "]";
    }
}
