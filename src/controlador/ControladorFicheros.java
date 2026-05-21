package controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import modelo.Cancion;
import modelo.Cliente;
import modelo.Playlist;
import vista.VentanaPrincipal;

/**
 * Controlador de ficheros compatible con el ejemplo original.
 */
public class ControladorFicheros {

    private final String ruta;

    public ControladorFicheros(String ruta) {
        this.ruta = ruta;
    }

    public ArrayList<Playlist> leerFichero(String nombreFichero) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        File fichero = new File(ruta + nombreFichero);

        try (BufferedReader lector = new BufferedReader(new FileReader(fichero))) {
            String linea;
            Playlist playlist = null;
            Cancion cancion = null;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();

                if (linea.startsWith("Playlist:")) {
                    if (playlist != null) {
                        playlists.add(playlist);
                    }
                    playlist = new Playlist();
                    playlist.setTitulo(linea.split(":", 2)[1].trim());
                    cancion = null;

                } else if (linea.startsWith("Nombre:")) {
                    cancion = new Cancion(0, "", "", 0, 0, 0, "", "cancion");
                    cancion.setNombreAudio(linea.split(":", 2)[1].trim());

                } else if (linea.startsWith("Duracion:")) {
                    if (cancion != null) {
                        cancion.setDuracionSegundos(parseDuracionSegundos(linea.split(":", 2)[1].trim()));
                    }

                } else if (linea.startsWith("Reproducciones:")) {
                    if (cancion != null && playlist != null) {
                        cancion.setReproducciones(Integer.parseInt(linea.split(":", 2)[1].trim()));
                        playlist.getCanciones().add(cancion);
                    }

                } else if (linea.startsWith("=====")) {
                    if (playlist != null) {
                        playlists.add(playlist);
                        playlist = null;
                        cancion = null;
                    }
                }
            }

            if (playlist != null) {
                playlists.add(playlist);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return playlists;
    }

    public void escribirFichero(String nombreFichero, ArrayList<Playlist> playlists) {
        try (BufferedWriter escribirFichero = new BufferedWriter(new FileWriter(ruta + nombreFichero, true))) {
            for (Playlist p : playlists) {
                escribirFichero.write("Playlist: " + p.getTitulo());
                escribirFichero.newLine();

                if (p.getCanciones() != null) {
                    for (Cancion c : p.getCanciones()) {
                        escribirFichero.write("Nombre: " + c.getNombreAudio());
                        escribirFichero.newLine();
                        escribirFichero.write("Duracion: " + c.duracionConvertida());
                        escribirFichero.newLine();
                        escribirFichero.write("Reproducciones: " + c.getReproducciones());
                        escribirFichero.newLine();
                        escribirFichero.write("-----");
                        escribirFichero.newLine();
                    }
                }

                escribirFichero.write("=====");
                escribirFichero.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escribirFicheroCancion(String nombreFichero, Cancion c) {
        try (BufferedWriter escribirFichero = new BufferedWriter(new FileWriter(ruta + nombreFichero, true))) {
            escribirFichero.write("Nombre: " + c.getNombreAudio());
            escribirFichero.newLine();
            escribirFichero.write("Duracion: " + c.duracionConvertida());
            escribirFichero.newLine();
            escribirFichero.write("Reproducciones: " + c.getReproducciones());
            escribirFichero.newLine();
            escribirFichero.write("-----");
            escribirFichero.newLine();
            escribirFichero.write("=====");
            escribirFichero.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cancion leerFicheroCancion(String nombreFichero) {
        Cancion c = null;

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta + nombreFichero))) {
            String linea;
            String nombre = "";
            int duracionSegundos = 0;
            int reproducciones = 0;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.startsWith("Nombre:")) {
                    nombre = linea.split(":", 2)[1].trim();
                } else if (linea.startsWith("Duracion:")) {
                    duracionSegundos = parseDuracionSegundos(linea.split(":", 2)[1].trim());
                } else if (linea.startsWith("Reproducciones:")) {
                    reproducciones = Integer.parseInt(linea.split(":", 2)[1].trim());
                }
            }

            c = new Cancion(0, nombre, "", duracionSegundos, reproducciones, 0, "", "cancion");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public void importarPlaylists(Cliente clienteLogeado, VentanaPrincipal ventana) {
        ArrayList<Playlist> playlistsActuales = ventana.getControladordb().obtenerPlaylists(clienteLogeado.getId());
        for (Playlist p : playlistsActuales) {
            ventana.getControladordb().borrarPlaylist(p.getTitulo());
        }

        ArrayList<Playlist> datosPlaylists = leerFichero("playlist.txt");

        for (Playlist p : datosPlaylists) {
            ventana.getControladordb().insertarPlaylist(p.getTitulo(), clienteLogeado.getId());
        }

        playlistsActuales = ventana.getControladordb().obtenerPlaylists(clienteLogeado.getId());

        for (Playlist pDB : playlistsActuales) {
            for (Playlist pDatos : datosPlaylists) {
                if (pDatos.getTitulo().equals(pDB.getTitulo())) {
                    for (Cancion c : pDatos.getCanciones()) {
                        ventana.getControladordb().insertarCancoinPlaylist(c.getId(), pDB.getId());
                    }
                }
            }
        }
    }

    public void exportarPlaylists(ArrayList<Playlist> playlistsActuales, Cliente clienteLogeado, VentanaPrincipal ventana) {
        for (Playlist p : playlistsActuales) {
            ArrayList<Cancion> canciones = ventana.getControladordb().obtenerCancionesPlaylist(p.getId());
            p.setCanciones(canciones);
            System.out.println("Playlist: " + p.getTitulo());
            System.out.println("Canciones encontradas: " + canciones.size());
        }

        escribirFichero("playlist.txt", playlistsActuales);
    }

    private int parseDuracionSegundos(String valor) {
        if (valor == null || valor.isBlank()) {
            return 0;
        }

        String texto = valor.trim();
        try {
            if (texto.contains(":")) {
                String[] partes = texto.split(":");
                if (partes.length == 3) {
                    int horas = Integer.parseInt(partes[0].trim());
                    int minutos = Integer.parseInt(partes[1].trim());
                    int segundos = Integer.parseInt(partes[2].trim());
                    return horas * 3600 + minutos * 60 + segundos;
                }
                if (partes.length == 2) {
                    int minutos = Integer.parseInt(partes[0].trim());
                    int segundos = Integer.parseInt(partes[1].trim());
                    return minutos * 60 + segundos;
                }
            }
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
