package modelo;

import java.util.Arrays;

public abstract class Artista {
    private int id;
    private String nombreArt;
    private String genero;
    private String descripcion;
    private String foto;

    public Artista() {
    }

    public Artista(int id, String nombreArt, String genero, String descripcion, String foto) {
        this.id = id;
        this.nombreArt = nombreArt;
        this.genero = genero;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdArtista() {
        return id;
    }

    public String getNombreArt() {
        return nombreArt;
    }

    public void setNombreArt(String nombreArt) {
        this.nombreArt = nombreArt;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Artista[id=" + id + ", nombreArt=" + nombreArt + ", genero=" + genero + ", descripcion=" + descripcion
                + ", foto=" + foto + "]";
    }
}