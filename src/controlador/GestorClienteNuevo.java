package controlador;

import modelo.*;
import java.util.*;

/**
 * Gestor centralizado de cliente.
 * Implementación única y reutilizable que delega en ControladorDB.
 */
public class GestorClienteNuevo {
    private ControladorDB controladordb;
    private Cliente clienteActual;
    private static final String NOMBRE_BD_DEFECTO = "spoty";

    public GestorClienteNuevo() {
        this(NOMBRE_BD_DEFECTO);
    }

    public GestorClienteNuevo(String nombreBD) {
        this.controladordb = new ControladorDB(nombreBD);
        this.clienteActual = null;
    }

    // ==================== AUTENTICACIÓN ====================

    public Cliente login(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return null;
        }

        usuario = usuario.trim();
        contrasena = contrasena.trim();

        if (!controladordb.startConnection()) return null;
        try {
            ArrayList<Cliente> clientes = controladordb.obtenerClientes();
            for (Cliente c : clientes) {
                if (usuario.equals(c.getUsuario()) && contrasena.equals(c.getContrasena())) {
                    c.setPlaylistCliente(controladordb.obtenerPlaylists(c.getId()));
                    this.clienteActual = c;
                    controladordb.setIdClienteActual(c.getId());
                    return c;
                }
            }
        } finally {
            controladordb.cerrarConexion();
        }
        return null;
    }

    public boolean registrarCliente(String nombre, String apellido, String usuario, String contrasena,
            String fechaNacimiento, String idioma) {
        if (!controladordb.startConnection()) return false;
        try {
            nombre = ControladorEntradaYSalida.letraMalluscula(nombre);
            apellido = ControladorEntradaYSalida.letraMalluscula(apellido);
            java.sql.Date fechaNac = java.sql.Date.valueOf(fechaNacimiento);
            return controladordb.sqlCrear(nombre, apellido, usuario, contrasena, fechaNac, idioma);
        } catch (Exception e) {
            System.out.println("Error en registro: " + e.getMessage());
            return false;
        } finally {
            controladordb.cerrarConexion();
        }
    }

    public boolean esAdmin(Cliente c) {
        return c != null && "admin".equals(c.getUsuario()) && "admin".equals(c.getContrasena());
    }

    public Cliente getClienteActual() {
        return clienteActual;
    }

    public void logout() {
        this.clienteActual = null;
        controladordb.cerrarConexion();
    }

    // ==================== CONSULTAS Y NAVEGACIÓN ====================

    public ArrayList<String> obtenerArtistas() {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.sqlArtistas(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Musico> obtenerMusicos() {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerMusicos(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Album> obtenerDiscografia(String nombreArtista) {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerAlbum(nombreArtista); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Cancion> obtenerCancionesAlbum(String nombreAlbum) {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerCanciones(nombreAlbum); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Podcaster> obtenerPodcasters() {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerPodcasters(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Podcast> obtenerPodcasts(String nombrePodcaster) {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerPodcasts(nombrePodcaster); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== PLAYLISTS ====================

    public ArrayList<Playlist> obtenerPlaylistsCliente() {
        if (clienteActual == null) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerPlaylists(clienteActual.getId()); } finally { controladordb.cerrarConexion(); }
    }

    public boolean crearPlaylist(String nombrePlaylist) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try {
            if (!clienteActual.isEsPremium()) {
                if (!controladordb.puedeCrearPlaylist(clienteActual.getId(), false)) {
                    return false;
                }
            }
            return controladordb.anadirPlaylist(nombrePlaylist);
        } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarPlaylist(String nombrePlaylist) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.borrarPlaylist(nombrePlaylist); } finally { controladordb.cerrarConexion(); }
    }

    public boolean agregarCancionPlaylist(String nombreCancion, String nombrePlaylist) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.anadirCancionPlaylist(nombreCancion, nombrePlaylist); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Cancion> obtenerCancionesPlaylist(int idPlaylist) {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerCancionesPlaylist(idPlaylist); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<Audio> obtenerAudiosDisponibles() {
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerAudios(); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== FAVORITOS ====================

    public ArrayList<Audio> obtenerFavoritosCliente() {
        if (clienteActual == null) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerFavoritos(clienteActual.getId()); } finally { controladordb.cerrarConexion(); }
    }

    public boolean estaEnFavoritosCliente(int idAudio) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.estaEnFavoritos(clienteActual.getId(), idAudio); } finally { controladordb.cerrarConexion(); }
    }

    public boolean agregarAFavoritosCliente(int idAudio) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.agregarAFavoritos(clienteActual.getId(), idAudio); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarDeFavoritosCliente(int idAudio) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarDeFavoritos(clienteActual.getId(), idAudio); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== REPRODUCCIÓN ====================

    public boolean reproducirCancion(int idAudio) {
        if (clienteActual == null) return false;
        if (!controladordb.startConnection()) return false;
        try {
            if (!controladordb.puedeReproducirCancion(clienteActual.getId(), idAudio, clienteActual.isEsPremium())) {
                return false;
            }
            return controladordb.registrarUltimaReproduccion(clienteActual.getId(), idAudio);
        } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarAPremium() {
        if (clienteActual == null) return false;
        clienteActual.setEsPremium(true);
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarCliente(clienteActual); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== MÉTODOS ADMIN ====================

    public boolean crearMusico(Musico musico) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { controladordb.insertarMusico(musico); return true; } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarArtista(int idArtista, String nombre, String genero, String descripcion, String imagen) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarArtista(idArtista, nombre, genero, descripcion, imagen); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarArtista(int idArtista) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarArtista(idArtista); } finally { controladordb.cerrarConexion(); }
    }

    public boolean crearAlbum(Album album) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { controladordb.insertarAlbum(album); return true; } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarAlbum(int idAlbum, String titulo, String ano, String genero, String imagen, int idMusico) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarAlbum(idAlbum, titulo, ano, genero, imagen, idMusico); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarAlbum(int idAlbum) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarAlbum(idAlbum); } finally { controladordb.cerrarConexion(); }
    }

    public boolean crearCancion(Cancion cancion) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { controladordb.insertarCancion(cancion); return true; } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarCancion(int idCancion, String nombre, String archivo, String duracion, int idAlbum, String artistasInvitados) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarCancion(idCancion, nombre, archivo, duracion, idAlbum, artistasInvitados); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarCancion(int idCancion) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarCancion(idCancion); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== PODCASTS ====================

    public boolean crearPodcaster(Podcaster podcaster) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { controladordb.insertarPodcaster(podcaster); return true; } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarPodcaster(int idPodcaster, String nombre, String descripcion, String imagen) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarPodcaster(idPodcaster, nombre, descripcion, imagen); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarPodcaster(int idPodcaster) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarPodcaster(idPodcaster); } finally { controladordb.cerrarConexion(); }
    }

    public boolean crearPodcast(Podcast podcast) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { controladordb.insertarPodcast(podcast); return true; } finally { controladordb.cerrarConexion(); }
    }

    public boolean actualizarPodcast(int idPodcast, String nombre, String archivo, int duracion, int numeroParticipantes, String descripcion, int idPodcaster) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.actualizarPodcast(idPodcast, nombre, archivo, duracion, numeroParticipantes, descripcion, idPodcaster); } finally { controladordb.cerrarConexion(); }
    }

    public boolean eliminarPodcast(int idPodcast) {
        if (clienteActual == null || !esAdmin(clienteActual)) return false;
        if (!controladordb.startConnection()) return false;
        try { return controladordb.eliminarPodcast(idPodcast); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== ESTADÍSTICAS ====================

    public ArrayList<EstadisticaCancion> obtenerEstadisticasCanciones() {
        if (clienteActual == null || !esAdmin(clienteActual)) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerEstadisticasCanciones(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<EstadisticaAudio> obtenerEstadisticasAudio() {
        if (clienteActual == null || !esAdmin(clienteActual)) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerEstadisticasAudio(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<EstadisticaPodcast> obtenerEstadisticasPodcast() {
        if (clienteActual == null || !esAdmin(clienteActual)) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerEstadisticasPodcast(); } finally { controladordb.cerrarConexion(); }
    }

    public ArrayList<EstadisticaPlaylist> obtenerEstadisticasPlaylist() {
        if (clienteActual == null || !esAdmin(clienteActual)) return new ArrayList<>();
        if (!controladordb.startConnection()) return new ArrayList<>();
        try { return controladordb.obtenerEstadisticasPlaylist(); } finally { controladordb.cerrarConexion(); }
    }

    // ==================== GETTER PARA CONTROLADOR DB ====================
    public ControladorDB getControladorDB() {
        return controladordb;
    }
}
