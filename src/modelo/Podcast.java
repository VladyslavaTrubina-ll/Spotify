package modelo;

import java.util.*;

/**
 * Clase que representa un podcast con información del podcaster y participantes.
 */
public class Podcast extends Audio {
    private int idPodcaster;
    private int numeroParticipantes;

    /**
     * Crea un podcast con todos sus datos.
     *
     * @param id identificador del audio
     * @param nombreAudio nombre del podcast
     * @param archivo archivo asociado
     * @param duracionSegundos duración en segundos
     * @param reproducciones número de reproducciones
     * @param idPodcaster id del podcaster
     * @param numeroParticipantes número de participantes
     * @param tipo tipo de audio
     */
    public Podcast(int id, String nombreAudio, String archivo, int duracionSegundos, int reproducciones, int idPodcaster, int numeroParticipantes, String tipo) {
        super(id, nombreAudio, archivo, reproducciones, duracionSegundos, tipo);
        this.idPodcaster = idPodcaster;
        this.numeroParticipantes = numeroParticipantes;
    }

    /** Obtiene el id del podcaster propietario. */
    public int getIdPodcaster() { return idPodcaster; }

    /** Establece el id del podcaster propietario. */
    public void setIdPodcaster(int idPodcaster) { this.idPodcaster = idPodcaster; }

    /** Obtiene el número de participantes del podcast. */
    public int getNumeroParticipantes() { return numeroParticipantes; }

    /** Establece el número de participantes. */
    public void setNumeroParticipantes(int numeroParticipantes) { this.numeroParticipantes = numeroParticipantes; }
}
