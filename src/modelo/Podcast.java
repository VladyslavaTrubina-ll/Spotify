package modelo;

import java.util.*;

public class Podcast extends Audio {
    private int idPodcaster;
    private int numeroParticipantes;

    public Podcast(int id, String nombreAudio, String archivo, int duratasecondi, int numRep, int idPodcaster, int numeroParticipantes, String tipo) {
        super(id, nombreAudio, archivo, numRep, duratasecondi, tipo);
        this.idPodcaster = idPodcaster;
        this.numeroParticipantes = numeroParticipantes;
    }

    public int getIdPodcaster() { return idPodcaster; }
    public void setIdPodcaster(int idPodcaster) { this.idPodcaster = idPodcaster; }
    public int getNumeroParticipantes() { return numeroParticipantes; }
    public void setNumeroParticipantes(int numeroParticipantes) { this.numeroParticipantes = numeroParticipantes; }
}
