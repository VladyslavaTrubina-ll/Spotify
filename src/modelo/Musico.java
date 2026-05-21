package modelo;

import java.util.*;

public class Musico extends Artista {
    private String composicion;

    public Musico() {}

    public Musico(int idArtista, String nombreArt, String genero, String descripcion, String foto, String composicion) {
    /**
     * Constructor de Musico que delega a Artista.
     *
     * @param idArtista id del artista
     */
        super(idArtista, nombreArt, genero, descripcion, foto);
        this.composicion = composicion;
    }

    /** Obtiene la característica/composición (p. ej. Solista/Grupo). */
    public String getComposicion() { return composicion; }
    /** Establece la composición del músico. */
    public void setComposicion(String composicion) { this.composicion = composicion; }

    @Override
    public String toString() {
        return "Musico[" + super.toString() + ", composicion=" + composicion + "]";
    }
}
