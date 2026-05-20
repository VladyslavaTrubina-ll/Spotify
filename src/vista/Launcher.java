package vista;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import modelo.*;
import controlador.*;

public class Launcher {

    private static final String NOMBRE_BD = "spoty";


        private static GestorCliente gestorCliente;
        private static ReproductorAudio reproductor;
        private static ControladorEntradaYSalida entrada;
        private static ArrayList<Audio> audiosReproductor;

        public static void main(String[] args) {
            entrada = new ControladorEntradaYSalida();
            gestorCliente = new GestorCliente(NOMBRE_BD);

            mostrarPantallaBienvenida();
            menuPrincipal();
        }
    private static void mostrarPantallaBienvenida() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      BIENVENIDO A SPOTIFY           	  ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    private static void menuPrincipal() {
        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│          MENÚ PRINCIPAL                │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│ 1. Iniciar Sesión                      │");
            System.out.println("│ 2. Registrar Nuevo Cliente             │");
            System.out.println("│ 0. Salir                               │");
            System.out.println("└────────────────────────────────────────┘");

            int opcion = entrada.esValorMenuValido(0, 2);

            switch (opcion) {
                case 1:
                    login();
                    break;
                case 2:
                    registrarse();
                    break;
                case 0:
                    System.out.println("\nHasta luego...");
                    return;
            }
        }
    }

    private static void login() {
        String usuario = entrada.leerCadena("Ingresa tu usuario: ");
        String contrasena = entrada.leerCadena("Ingresa tu contraseña: ");

        Cliente cliente = gestorCliente.login(usuario, contrasena);
        if (cliente != null) {
            System.out.println("\n¡Bienvenido, " + gestorCliente.getClienteActual().getNombre() + "!");
            menuCliente();
        } else {
            System.out.println("\nUsuario o contraseña incorrectos.");
        }
    }

    private static void registrarse() {
        String nombre = entrada.leerCadena("Nombre: ");
        String apellido = entrada.leerCadena("Apellido: ");
        String usuario = entrada.leerCadena("Usuario: ");
        String contrasena = entrada.leerCadena(" Contraseña: ");
        System.out.println("\nIdiomas disponibles: 1. Español  2. English  3. Ruso  4. Portugués");
        int opcionIdioma = entrada.esValorMenuValido(1, 4);
        String idioma = switch (opcionIdioma) {
            case 1 -> "Español";
            case 2 -> "English";
            case 3 -> "Ruso";
            default -> "Portugués";
        };

        // Pedir fecha de nacimiento (formato YYYY-MM-DD)
        String fechaStr = entrada.leerCadena("Fecha de nacimiento (YYYY-MM-DD): ");
        try {
            if (gestorCliente.registrarCliente(nombre, apellido, usuario, contrasena, fechaStr, idioma)) {
                System.out.println("\n¡Registro exitoso! Ya puedes iniciar sesión.");
            } else {
                System.out.println("\nError al registrar.");
            }
        } catch (Exception e) {
            System.out.println("\nFormato de fecha inválido. Usa YYYY-MM-DD");
        }
    }

    private static void menuCliente() {
        Cliente cliente = gestorCliente.getClienteActual();
        if (cliente == null) return;

        
        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│  MENÚ CLIENTE " + (cliente.isEsPremium() ? "(PREMIUM)" : "(FREE)") + "              │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│ 1. Explorar Música                  │");
            System.out.println("│ 2. Explorar Podcasts               │");
            System.out.println("│ 3. Mis Favoritos                     │");
            System.out.println("│ 4. Mis Playlists                    │");
            System.out.println("│ 5. Reproductor de Audio            │");
            System.out.println("│ 6. Actualizar a Premium             │");
            System.out.println("│ 7. Cerrar Sesión                    │");
            System.out.println("└────────────────────────────────────────┘");

            int opcion = entrada.esValorMenuValido(1, 7);

            switch (opcion) {
                case 1:
                    explorarMusica();
                    break;
                case 2:
                    explorarPodcasts();
                    break;
                case 3:
                    verFavoritos();
                    break;
                case 4:
                    gestionarPlaylists();
                    break;
                case 5:
                    iniciarReproductor();
                    break;
                case 6:
                    actualizarPremium();
                    break;
                case 7:
                    gestorCliente.logout();
                    System.out.println("\nSesión cerrada. Hasta luego!");
                    return;
            }
        }
    }

    private static void explorarMusica() {
        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│      EXPLORAR MÚSICA                   │");
            System.out.println("└────────────────────────────────────────┘");

            ArrayList<String> artistas = gestorCliente.obtenerArtistas();
            if (artistas.isEmpty()) {
                System.out.println("No hay artistas disponibles");
                entrada.leerCadena("Presiona Enter para volver...");
                return;
            }

            
            System.out.println("\n Artistas disponibles:");
            for (int i = 0; i < artistas.size(); i++) {
                System.out.printf(" %2d) %s\n", (i + 1), artistas.get(i));
            }
            System.out.println("\n  0. Volver");
            int opcion = entrada.esValorMenuValido(0, artistas.size());
            if (opcion == 0) {
                return;
            }
            
            String artista = artistas.get(opcion - 1);
            ArrayList<Album> albums = gestorCliente.obtenerDiscografia(artista);

            if (albums.isEmpty()) {
                System.out.println("\nEl artista '" + artista + "' no tiene álbumes cargados.");
                System.out.println("\n1. Elegir otro artista");
                System.out.println("2. Volver al menú cliente");
                int op = entrada.esValorMenuValido(1, 2);
                if (op == 1) {
                    // Volver a la lista de artistas
                    continue;
                } else {
                    // Regresar al menú del cliente
                    return;
                }
            }

            System.out.println("\nÁlbumes de " + artista + ":");
            for (int i = 0; i < albums.size(); i++) {
                System.out.printf(" %2d) %s (%s)\n", (i + 1), albums.get(i).getTitulo(), albums.get(i).getAnno());
            }

            int opAlbum = entrada.esValorMenuValido(0, albums.size());
            if (opAlbum == 0) {
                continue;
            }

            String albumTitulo = albums.get(opAlbum - 1).getTitulo();
            ArrayList<Cancion> canciones = gestorCliente.obtenerCancionesAlbum(albumTitulo);

            if (canciones.isEmpty()) {
                System.out.println("\nNo hay canciones en este álbum.");
                entrada.leerCadena("Enter para volver...");
                continue;
            }

            System.out.println("\nCanciones en '" + albumTitulo + "':");
            for (int i = 0; i < canciones.size(); i++) {
                System.out.printf(" %2d) %s\n", (i + 1), canciones.get(i).getNombreAudio());
            }

            entrada.leerCadena("Presiona Enter para volver al menú de música...");
        }
    }

    private static void explorarPodcasts() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      EXPLORAR PODCASTS            │");
        System.out.println("└────────────────────────────────────────┘");

        var podcasters = gestorCliente.obtenerPodcasters().stream()
            .map(p -> p.getNombreArt())
                .collect(Collectors.toList());

        if (podcasters.isEmpty()) {
            System.out.println("No hay podcasters disponibles");
            return;
        }

            System.out.println("\nPodcasters disponibles:");
            for (int i = 0; i < podcasters.size(); i++) {
                System.out.println((i + 1) + ". " + podcasters.get(i));
            }

            System.out.println("\n0. Volver");
            int opcion = entrada.esValorMenuValido(0, podcasters.size());
            if (opcion == 0) return;

            String elegido = podcasters.get(opcion - 1);
            ArrayList<Podcast> podcasts = gestorCliente.obtenerPodcasts(elegido);

            if (podcasts.isEmpty()) {
                System.out.println("\nEl podcaster '" + elegido + "' no tiene podcasts cargados.");
                System.out.println("1. Elegir otro podcaster");
                System.out.println("2. Volver al menú cliente");
                int op = entrada.esValorMenuValido(1, 2);
                if (op == 1) 
                	return ;
                else return;
            }

            // Listar episodios
            System.out.println("\nPodcasts de " + elegido + ":");
            for (int i = 0; i < podcasts.size(); i++) {
                Podcast pd = podcasts.get(i);
                System.out.println((i + 1) + ". " + pd.getNombreAudio() + " [" + pd.durataConvertida() + "] - " + pd.getArchivo());
            }

            System.out.println("\n1. Reproducir episodio");
            System.out.println("2. Descargar episodio");
            System.out.println("3. Volver a podcasters");
            int accion = entrada.esValorMenuValido(1, 3);
            if (accion == 3) return;

            if (accion == 1) {
                System.out.println("\nElige un episodio para reproducir:");
                int sel = entrada.esValorMenuValido(1, podcasts.size());
                // Preparar reproductor si es necesario
                Cliente cliente = gestorCliente.getClienteActual();
                if (reproductor == null && cliente != null) {
                    reproductor = new ReproductorAudio(cliente.getId(), cliente.isEsPremium(), new controlador.ControladorDB(NOMBRE_BD), gestorCliente);
                }
                ArrayList<Audio> cola = new ArrayList<>();
                cola.addAll(podcasts);
                reproductor.establecerColaReproduccion(cola);
                reproductor.saltarA(sel - 1);
                reproductor.play();
                System.out.println(reproductor.obtenerInformacionActual());
            } else if (accion == 2) {
                System.out.println("\nElige un episodio para descargar:");
                int sel = entrada.esValorMenuValido(1, podcasts.size());
                Podcast pd = podcasts.get(sel - 1);
                Path src = Paths.get(pd.getArchivo());
                Path downloads = Paths.get("downloads");
                try {
                    if (!Files.exists(downloads)) Files.createDirectories(downloads);
                    Path dest = downloads.resolve(src.getFileName());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Episodio guardado en: " + dest.toString());
                } catch (IOException e) {
                    System.out.println("Error guardando episodio: " + e.getMessage());
                }
            }
        }

    private static void verFavoritos() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      MIS FAVORITOS                 │");
        System.out.println("└────────────────────────────────────────┘");

        ArrayList<Audio> favoritos = gestorCliente.obtenerFavoritosCliente();
        if (favoritos.isEmpty()) {
            System.out.println("No tienes favoritos aún");
            return;
        }

        System.out.println("\nTus favoritos (" + favoritos.size() + "):");
        for (int i = 0; i < favoritos.size(); i++) {
            System.out.println((i + 1) + ". " + favoritos.get(i).getNombreAudio());
        }
    }

    private static void gestionarPlaylists() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│       MIS PLAYLISTS                │");
        System.out.println("└────────────────────────────────────────┘");

        ArrayList<Playlist> playlists = gestorCliente.obtenerPlaylistsCliente();
        System.out.println("\nTus playlists (" + playlists.size() + "):");
        for (Playlist p : playlists) {
            System.out.println(" - " + p.getTitulo());
        }

        System.out.println("\n1. Crear nueva playlist");
        System.out.println("2. Volver");
        int opcion = entrada.esValorMenuValido(1, 2);

        if (opcion == 1) {
            String nombre = entrada.leerCadena("Nombre de la playlist: ");
            if (gestorCliente.crearPlaylist(nombre)) {
                System.out.println("\nPlaylist creada exitosamente");
            } else {
                System.out.println("\nNo se pudo crear la playlist");
            }
        }
    }

    private static void iniciarReproductor() {
        Cliente cliente = gestorCliente.getClienteActual();
        if (cliente == null) {
            System.out.println("\nDebes iniciar sesión primero.");
            return;
        }

        audiosReproductor = crearAudiosDemo();
        if (audiosReproductor.isEmpty()) {
            System.out.println("\nNo hay canciones disponibles.");
            return;
        }

        reproductor = new ReproductorAudio(cliente.getId(), cliente.isEsPremium(), new controlador.ControladorDB(NOMBRE_BD), gestorCliente);
        reproductor.establecerColaReproduccion(audiosReproductor);

        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│      REPRODUCTOR DE AUDIO              │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.println("\nCanciones disponibles:");
            for (int i = 0; i < audiosReproductor.size(); i++) {
                Audio audio = audiosReproductor.get(i);
                System.out.println((i + 1) + ". " + audio.getNombreAudio() + " [" + audio.durataConvertida() + "] - " + audio.getArchivo());
            }

            System.out.println("\n1. Reproducir canción");
            System.out.println("2. Pausar");
            System.out.println("3. Siguiente");
            System.out.println("4. Anterior");
            System.out.println("5. Cambiar velocidad");
            System.out.println("6. Volver al menú");
            System.out.println("7. Descargar canción (guardar WAV en downloads/)");

            int opcion = entrada.esValorMenuValido(1, 7);

            if (opcion == 6) {
                reproductor.detener();
                return;
            }

            if (opcion == 1) {
                System.out.println("\nElige una canción para reproducir:");
                int seleccion = entrada.esValorMenuValido(1, audiosReproductor.size());
                reproductor.saltarA(seleccion - 1);
                reproductor.play();
                System.out.println(reproductor.obtenerInformacionActual());
            } else if (opcion == 2) {
                reproductor.pause();
                System.out.println("Pausado.");
            } else if (opcion == 3) {
                Audio audio = reproductor.siguiente();
                if (audio != null) {
                    reproductor.play();
                    System.out.println("Ahora suena: " + audio.getNombreAudio());
                }
            } else if (opcion == 4) {
                Audio audio = reproductor.anterior();
                if (audio != null) {
                    reproductor.play();
                    System.out.println("Ahora suena: " + audio.getNombreAudio());
                }
            } else if (opcion == 5) {
                System.out.println("\nVelocidades: 1. 0.5x  2. 1x  3. 1.5x  4. 2x");
                int vel = entrada.esValorMenuValido(1, 4);
                double velocidad = switch (vel) {
                    case 1 -> 0.5;
                    case 2 -> 1.0;
                    case 3 -> 1.5;
                    default -> 2.0;
                };
                reproductor.establecerVelocidad(velocidad);
            } else if (opcion == 7) {
                System.out.println("\nElige una canción para descargar:");
                int seleccion = entrada.esValorMenuValido(1, audiosReproductor.size());
                Audio audio = audiosReproductor.get(seleccion - 1);
                Path src = Paths.get(audio.getArchivo());
                Path downloads = Paths.get("downloads");
                try {
                    if (!Files.exists(downloads)) Files.createDirectories(downloads);
                    Path dest = downloads.resolve(src.getFileName());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Canción guardada en: " + dest.toString());
                } catch (IOException e) {
                    System.out.println("Error guardando canción: " + e.getMessage());
                }
            }
        }
    }

    private static ArrayList<Audio> crearAudiosDemo() {
        ArrayList<Audio> audios = new ArrayList<>();
        audios.add(new Cancion(1, "Cancion 1", "canciones/cancion1.wav", 12, 0, 0, null, "Cancion", "imagenes/imagen1.png"));
        audios.add(new Cancion(2, "Cancion 2", "canciones/cancion2.wav", 12, 0, 0, null, "Cancion", "imagenes/imagen2.gif"));
        return audios;
    }

    private static void actualizarPremium() {
        Cliente cliente = gestorCliente.getClienteActual();
        if (cliente.isEsPremium()) {
            System.out.println("\nYa eres usuario Premium");
            return;
        }

        String confirmacion = entrada.leerSiNo("¿Deseas actualizarte a Premium?");
        if (confirmacion.equalsIgnoreCase("si")) {
            if (gestorCliente.actualizarAPremium()) {
                System.out.println("\n¡Bienvenido a Premium!");
                System.out.println("Ahora disfrutas de:");
                System.out.println(" - Playlists ilimitadas");
                System.out.println(" - Sin restricción de 10 minutos");
                System.out.println(" - Reproducción ordenada o aleatoria");
            } else {
                System.out.println("\nError al actualizar Premium");
            }
        }
    }
}
