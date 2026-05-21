package modelo;

import java.util.*;

/**
 * Clase base abstracta para contenido de audio (canciones y podcasts).
 * Define propiedades comunes y comportamientos de reproducción.
 */
public abstract class Audio {
    private int id;
    private String nombreAudio;
    private String archivo;
    private int reproducciones;
    private int duracionSegundos;
    private String tipo;

    public Audio() {}

    public Audio(int id, String nombreAudio, String archivo, int reproducciones, int duracionSegundos, String tipo) {
        this.id = id;
        this.nombreAudio = nombreAudio;
        this.archivo = archivo;
        this.reproducciones = Math.max(0, reproducciones);
        this.duracionSegundos = Math.max(0, duracionSegundos);
        this.tipo = tipo;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    
    public String getArchivo() { return archivo; }
    public void setArchivo(String archivo) { this.archivo = archivo; }
    
    public int getReproducciones() { return reproducciones; }
    public void setReproducciones(int reproducciones) { this.reproducciones = Math.max(0, reproducciones); }
    
    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = Math.max(0, duracionSegundos); }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Convierte la duración en segundos a formato MM:SS
     */
    public String duracionConvertida() {
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    @Override
    public String toString() {
        return "Audio[id=" + id + ", nombre=" + nombreAudio + ", tipo=" + tipo + ", duracion=" + duracionConvertida() + "]";
    }
}
