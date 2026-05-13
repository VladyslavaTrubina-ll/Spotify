package modelo;

import java.util.*;

public abstract class Audio {
    private int id;
    private String nombreAudio;
    private String archivo;
    private int numRep;
    private int duratasecondi;
    private String tipo;

    public Audio() {}

    public Audio(int id, String nombreAudio, String archivo, int numRep, int duratasecondi, String tipo) {
        this.id = id;
        this.nombreAudio = nombreAudio;
        this.archivo = archivo;
        this.numRep = Math.max(0, numRep);
        this.duratasecondi = Math.max(0, duratasecondi);
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreAudio() { return nombreAudio; }
    public void setNombreAudio(String nombreAudio) { this.nombreAudio = nombreAudio; }
    public String getArchivo() { return archivo; }
    public void setArchivo(String archivo) { this.archivo = archivo; }
    public int getNumRep() { return numRep; }
    public int getNreproduciones() { return numRep; }
    public void setNumRep(int numRep) { this.numRep = Math.max(0, numRep); }
    public int getDuratasecondi() { return duratasecondi; }
    public void setDuratasecondi(int duratasecondi) { this.duratasecondi = Math.max(0, duratasecondi); }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String durataConvertida() {
        int minuti = duratasecondi / 60;
        int secondi = duratasecondi % 60;
        return String.format("%d:%02d", minuti, secondi);
    }

    @Override
    public String toString() {
        return "Audio[id=" + id + ", nombre=" + nombreAudio + ", tipo=" + tipo + ", duracion=" + durataConvertida() + "]";
    }
}
