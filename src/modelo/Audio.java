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

    /**
     * Crea un audio vacío.
     */
    public Audio() {}

    /**
     * Crea un audio con todos sus datos básicos.
     *
     * @param id identificador del audio
     * @param nombreAudio nombre del audio
     * @param archivo ruta o nombre del archivo
     * @param reproducciones número de reproducciones
     * @param duracionSegundos duración en segundos
     * @param tipo tipo de audio
     */
    public Audio(int id, String nombreAudio, String archivo, int reproducciones, int duracionSegundos, String tipo) {
        this.id = id;
        this.nombreAudio = nombreAudio;
        this.archivo = archivo;
        this.reproducciones = Math.max(0, reproducciones);
        this.duracionSegundos = Math.max(0, duracionSegundos);
        this.tipo = tipo;
    }

    /** Obtiene el identificador del audio. */
    public int getId() { return id; }

    /** Establece el identificador del audio. */
    public void setId(int id) { this.id = id; }

    /** Obtiene el nombre del audio. */
    public String getNombreAudio() { return nombreAudio; }

    /** Establece el nombre del audio. */
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }

    /** Obtiene la ruta del archivo asociado. */
    public String getArchivo() { return archivo; }

    /** Establece la ruta del archivo asociado. */
    public void setArchivo(String archivo) { this.archivo = archivo; }

    /** Obtiene el número de reproducciones. */
    public int getReproducciones() { return reproducciones; }

    /** Establece el número de reproducciones. */
    public void setReproducciones(int reproducciones) { this.reproducciones = Math.max(0, reproducciones); }

    /** Obtiene la duración en segundos. */
    public int getDuracionSegundos() { return duracionSegundos; }

    /** Establece la duración en segundos. */
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = Math.max(0, duracionSegundos); }

    /** Obtiene el tipo de audio. */
    public String getTipo() { return tipo; }

    /** Establece el tipo de audio. */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Convierte la duración en segundos a formato MM:SS.
     *
     * @return duración formateada
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
