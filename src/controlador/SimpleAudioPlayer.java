package controlador;

import javax.sound.sampled.*;
import java.io.File;

public class SimpleAudioPlayer {
    private Clip clip;
    private long pausePos = 0;

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

    public void play() {
        if (clip == null) return;
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    public void pause() {
        if (clip == null || !clip.isRunning()) return;
        pausePos = clip.getMicrosecondPosition();
        clip.stop();
    }

    public void resume() {
        if (clip == null) return;
        clip.setMicrosecondPosition(pausePos);
        clip.start();
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
        clip.setMicrosecondPosition(0);
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
            pausePos = 0;
        }
    }
}
