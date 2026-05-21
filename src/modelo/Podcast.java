package modelo;

import java.util.*;

/**
 * Clase que representa un podcast con información del podcaster y participantes.
 */
public class Podcast extends Audio {
    private int idPodcaster;
    private int numeroParticipantes;

    public Podcast(int id, String nombreAudio, String archivo, int duracionSegundos, int reproducciones, int idPodcaster, int numeroParticipantes, String tipo) {
        super(id, nombreAudio, archivo, reproducciones, duracionSegundos, tipo);
        this.idPodcaster = idPodcaster;
        this.numeroParticipantes = numeroParticipantes;
    }

    public int getIdPodcaster() { return idPodcaster; }
    public void setIdPodcaster(int idPodcaster) { this.idPodcaster = idPodcaster; }
    public int getNumeroParticipantes() { return numeroParticipantes; }
    public void setNumeroParticipantes(int numeroParticipantes) { this.numeroParticipantes = numeroParticipantes; }
}
