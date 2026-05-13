package modelo;

import java.util.*;

public class Podcaster extends Artista {

    public Podcaster() {}

    public Podcaster(int idArtista, String nombreArt, String genero, String descripcion, String foto) {
        super(idArtista, nombreArt, genero, descripcion, foto);
    }

    @Override
    public String toString() {
        return "Podcaster[" + super.toString() + "]";
    }
}
