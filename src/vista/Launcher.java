package vista;

import controlador.ControladorDB;
import java.sql.Date;
import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {
        ControladorDB controladorDB = new ControladorDB("spoty");
        
        if (controladorDB.startConnection()) {
            System.out.println("✓ Conexión a la base de datos establecida.\n");
            
            // Prueba 1: Listar artistas
            System.out.println("--- Prueba 1: Listando artistas ---");
            ArrayList<String> artistas = controladorDB.sqlArtistas();
            for (String artista : artistas) {
                System.out.println("  - " + artista);
            }
            
            // Prueba 2: Login con usuario existente
            System.out.println("\n--- Prueba 2: Intentando login ---");
            boolean loginExitoso = controladorDB.iniciarSesion("enigma", "pass1");
            if (loginExitoso) {
                System.out.println("✓ Login exitoso para usuario: enigma");
            } else {
                System.out.println("✗ Login fallido");
            }
            
            // Prueba 3: Crear nueva playlist
            System.out.println("\n--- Prueba 3: Creando playlist ---");
            boolean playlistCreada = controladorDB.anadirPlaylist("Mi Playlist de Prueba");
            if (playlistCreada) {
                System.out.println("✓ Playlist creada exitosamente");
            } else {
                System.out.println("✗ No se pudo crear playlist");
            }
            
            // Prueba 4: Añadir canción a playlist
            System.out.println("\n--- Prueba 4: Añadiendo canción a playlist ---");
            boolean cancionAnadida = controladorDB.anadirCancionPlaylist("Cyber Dream", "Mi Playlist de Prueba");
            if (cancionAnadida) {
                System.out.println("✓ Canción añadida a la playlist");
            } else {
                System.out.println("✗ No se pudo añadir canción");
            }
            
            // Prueba 5: Registrar nuevo cliente
            System.out.println("\n--- Prueba 5: Registrando nuevo cliente ---");
            Date fechaNacimiento = Date.valueOf("2000-01-15");
            boolean clienteCreado = controladorDB.sqlCrear(
                "Juan", 
                "Pérez", 
                "juanperez2024", 
                "mipassword123", 
                fechaNacimiento, 
                "1"  // Español
            );
            if (clienteCreado) {
                System.out.println("✓ Cliente registrado exitosamente");
            } else {
                System.out.println("✗ No se pudo registrar cliente");
            }
            
            // Prueba 6: Borrar playlist
            System.out.println("\n--- Prueba 6: Borrando playlist ---");
            boolean playlistBorrada = controladorDB.borrarPlaylist("Mi Playlist de Prueba");
            if (playlistBorrada) {
                System.out.println("✓ Playlist borrada exitosamente");
            } else {
                System.out.println("✗ No se pudo borrar playlist");
            }
            
            System.out.println("\n--- Cerrando conexión ---");
            controladorDB.cerrarConexion();
            System.out.println("✓ Conexión cerrada.\n");
            
        } else {
            System.out.println("✗ No se pudo establecer conexión a la base de datos.");
        }
    }

}
