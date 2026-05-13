package modelo;

import java.util.*;

public class Musico extends Artista {
    private String composicion;

    public Musico() {}

    public Musico(int idArtista, String nombreArt, String genero, String descripcion, String foto, String composicion) {
        super(idArtista, nombreArt, genero, descripcion, foto);
        this.composicion = composicion;
    }

    public String getComposicion() { return composicion; }
    public void setComposicion(String composicion) { this.composicion = composicion; }

    @Override
    public String toString() {
        return "Musico[" + super.toString() + ", composicion=" + composicion + "]";
    }
}
