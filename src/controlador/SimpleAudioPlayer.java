package controlador;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Reproductor de audio simple que encapsula un `Clip` de Java Sound.
 * Proporciona operaciones básicas: cargar, reproducir, pausar, reanudar
 * y cerrar. Diseñado para pruebas y reproducción local de ficheros.
 */
public class SimpleAudioPlayer {
    private Clip clip;
    private long pausePos = 0;

    /**
     * Carga un fichero de audio desde la ruta indicada en un `Clip`.
     *
     * @param path ruta del fichero de audio
     * @return true si la carga fue exitosa
     */
    public boolean load(String path) {
        try {
            close();
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(ais);
            return true;
        } catch (Exception e) {
            System.out.println("Error cargando audio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicia la reproducción desde el principio.
     */
    public void play() {
        if (clip == null) return;
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    /**
     * Pausa la reproducción guardando la posición.
     */
    public void pause() {
        if (clip == null || !clip.isRunning()) return;
        pausePos = clip.getMicrosecondPosition();
        clip.stop();
    }

    /**
     * Reanuda la reproducción desde la posición en la que se pausó.
     */
    public void resume() {
        if (clip == null) return;
        clip.setMicrosecondPosition(pausePos);
        clip.start();
    }

    /**
     * Detiene la reproducción y resetea la posición.
     */
    public void stop() {
        if (clip == null) return;
        clip.stop();
        clip.setMicrosecondPosition(0);
    }

    /**
     * Indica si actualmente se está reproduciendo audio.
     *
     * @return true si hay reproducción en curso
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Cierra el clip liberando recursos.
     */
    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
            pausePos = 0;
        }
    }
}
