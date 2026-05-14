package vista;

import controlador.ControladorDB;
import controlador.ControladorEntradaYSalida;
import controlador.GestorCliente;
import java.sql.Date;
import java.util.ArrayList;
import modelo.Album;
import modelo.Cancion;

import controlador.ReproductorAudio;
import modelo.Audio;
import modelo.Cliente;
import modelo.Playlist;
import java.util.stream.Collectors;
public class Launcher {


        private static GestorCliente gestorCliente;
        private static ReproductorAudio reproductor;
        private static ControladorEntradaYSalida entrada;

        public static void main(String[] args) {
            entrada = new ControladorEntradaYSalida();
            gestorCliente = new GestorCliente();

            mostrarPantallaBienvenida();
            menuPrincipal();
        }
    private static void mostrarPantallaBienvenida() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      ♫ BIENVENIDO A SPOTIFY ♫         ║");
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
                    System.out.println("\n✗ Hasta luego...");
                    return;
            }
        }
    }

    private static void login() {
        String usuario = entrada.leerCadena("👤 Ingresa tu usuario: ");
        String contrasena = entrada.leerCadena("🔐 Ingresa tu contraseña: ");

        if (gestorCliente.login(usuario, contrasena)) {
            System.out.println("\n✓ ¡Bienvenido, " + gestorCliente.getClienteActual().getNombre() + "!");
            menuCliente();
        } else {
            System.out.println("\n✗ Usuario o contraseña incorrectos.");
        }
    }

    private static void registrarse() {
        String nombre = entrada.leerCadena("📝 Nombre: ");
        String apellido = entrada.leerCadena("📝 Apellido: ");
        String usuario = entrada.leerCadena("👤 Usuario: ");
        String contrasena = entrada.leerCadena("🔐 Contraseña: ");
        String dni = entrada.leerDNI("\n📋 DNI (formato: XXXXXXXX-X o similar): ");

        // Pedir fecha de nacimiento (formato YYYY-MM-DD)
        String fechaStr = entrada.leerCadena("📅 Fecha de nacimiento (YYYY-MM-DD): ");
        try {
            Date fecha = Date.valueOf(fechaStr);
            if (gestorCliente.registrarCliente(nombre, apellido, usuario, contrasena, fecha, dni)) {
                System.out.println("\n✓ ¡Registro exitoso! Ya puedes iniciar sesión.");
            } else {
                System.out.println("\n✗ Error al registrar.");
            }
        } catch (Exception e) {
            System.out.println("\n✗ Formato de fecha inválido. Usa YYYY-MM-DD");
        }
    }

    private static void menuCliente() {
        Cliente cliente = gestorCliente.getClienteActual();
        if (cliente == null) return;

        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│  ♫ MENÚ CLIENTE " + (cliente.isEsPremium() ? "(PREMIUM)" : "(FREE)") + "              │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│ 1. 🎵 Explorar Música                  │");
            System.out.println("│ 2. 🎙️  Explorar Podcasts               │");
            System.out.println("│ 3. ♡ Mis Favoritos                     │");
            System.out.println("│ 4. 📋 Mis Playlists                    │");
            System.out.println("│ 5. ⏯️  Reproductor de Audio            │");
            System.out.println("│ 6. 💎 Actualizar a Premium             │");
            System.out.println("│ 7. 🚪 Cerrar Sesión                    │");
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
                    System.out.println("\n✓ Sesión cerrada. Hasta luego!");
                    return;
            }
        }
    }

    private static void explorarMusica() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      🎵 EXPLORAR MÚSICA               │");
        System.out.println("└────────────────────────────────────────┘");

        ArrayList<String> artistas = gestorCliente.obtenerArtistas();
        if (artistas.isEmpty()) {
            System.out.println("✗ No hay artistas disponibles");
            return;
        }

        System.out.println("\n📍 Artistas disponibles:");
        for (int i = 0; i < artistas.size(); i++) {
            System.out.println((i + 1) + ". " + artistas.get(i));
        }

        int opcion = entrada.esValorMenuValido(1, artistas.size());
        String artista = artistas.get(opcion - 1);

        ArrayList<Album> albums = gestorCliente.obtenerDiscografia(artista);
        System.out.println("\n📀 Álbumes de " + artista + ":");
        for (int i = 0; i < albums.size(); i++) {
            System.out.println((i + 1) + ". " + albums.get(i).getTitulo() + " (" + albums.get(i).getAño() + ")");
        }

        if (!albums.isEmpty()) {
            int opAlbum = entrada.esValorMenuValido(1, albums.size());
            String albumTitulo = albums.get(opAlbum - 1).getTitulo();

            ArrayList<Cancion> canciones = gestorCliente.obtenerCancionesAlbum(albumTitulo);
            System.out.println("\n🎵 Canciones:");
            for (int i = 0; i < canciones.size(); i++) {
                System.out.println((i + 1) + ". " + canciones.get(i).getNombre());
            }
        }
    }

    private static void explorarPodcasts() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      🎙️  EXPLORAR PODCASTS            │");
        System.out.println("└────────────────────────────────────────┘");

        var podcasters = gestorCliente.obtenerPodcasters().stream()
                .map(p -> p.getNombre())
                .collect(Collectors.toList());

        if (podcasters.isEmpty()) {
            System.out.println("✗ No hay podcasters disponibles");
            return;
        }

        System.out.println("\n🎙️  Podcasters disponibles:");
        for (int i = 0; i < podcasters.size(); i++) {
            System.out.println((i + 1) + ". " + podcasters.get(i));
        }
    }

    private static void verFavoritos() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      ♡ MIS FAVORITOS                 │");
        System.out.println("└────────────────────────────────────────┘");

        ArrayList<Audio> favoritos = gestorCliente.obtenerFavoritosCliente();
        if (favoritos.isEmpty()) {
            System.out.println("✗ No tienes favoritos aún");
            return;
        }

        System.out.println("\n♡ Tus favoritos (" + favoritos.size() + "):");
        for (int i = 0; i < favoritos.size(); i++) {
            System.out.println((i + 1) + ". " + favoritos.get(i).getNombre());
        }
    }

    private static void gestionarPlaylists() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│      📋 MIS PLAYLISTS                │");
        System.out.println("└────────────────────────────────────────┘");

        ArrayList<Playlist> playlists = gestorCliente.obtenerPlaylistsCliente();
        System.out.println("\n📋 Tus playlists (" + playlists.size() + "):");
        for (Playlist p : playlists) {
            System.out.println(" - " + p.getNombre());
        }

        System.out.println("\n1. Crear nueva playlist");
        System.out.println("2. Volver");
        int opcion = entrada.esValorMenuValido(1, 2);

        if (opcion == 1) {
            String nombre = entrada.leerCadena("📝 Nombre de la playlist: ");
            if (gestorCliente.crearPlaylist(nombre)) {
                System.out.println("\n✓ Playlist creada exitosamente");
            } else {
                System.out.println("\n✗ No se pudo crear la playlist");
            }
        }
    }

    private static void iniciarReproductor() {
        System.out.println("\n⏯️  Reproductor iniciado (función en desarrollo)");
    }

    private static void actualizarPremium() {
        Cliente cliente = gestorCliente.getClienteActual();
        if (cliente.isEsPremium()) {
            System.out.println("\n✓ Ya eres usuario Premium");
            return;
        }

        String confirmacion = entrada.leerSiNo("¿Deseas actualizarte a Premium?");
        if (confirmacion.equalsIgnoreCase("si")) {
            if (gestorCliente.actualizarAPremium()) {
                System.out.println("\n✓ ¡Bienvenido a Premium!");
                System.out.println("Ahora disfrutas de:");
                System.out.println(" - Playlists ilimitadas");
                System.out.println(" - Sin restricción de 10 minutos");
                System.out.println(" - Reproducción ordenada o aleatoria");
            } else {
                System.out.println("\n✗ Error al actualizar Premium");
            }
        }
    }
}
