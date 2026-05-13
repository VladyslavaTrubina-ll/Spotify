package modelo;

public class StatisticaAudio {
    private int idAudio;
    private int reproducioines;
    private String nombreAudio;

    public StatisticaAudio(int idAudio, String nombreAudio, int reproduciones) {
        this.idAudio = idAudio;
        this.reproducioines = reproduciones;
        this.nombreAudio = nombreAudio;
    }

    @Override
    public String toString() {
        return "StatisticaAudio [idAudio=" + idAudio + ", reproducioines=" + reproducioines + ", nombreAudio=" + nombreAudio
                + "]";
    }

    public int getIdAudio() { return idAudio; }
    public void setIdAudio(int idAudio) { this.idAudio = idAudio; }
    public int getReproducioines() { return reproducioines; }
    public void setReproducioines(int reproducioines) { this.reproducioines = reproducioines; }
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }

    // Alias getters for compatibility with previous naming
    public int getId() { return idAudio; }
    public String getNombre() { return nombreAudio; }
    public int getReproducciones() { return reproducioines; }
}
