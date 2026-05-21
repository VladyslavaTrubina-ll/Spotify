package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Musico;
import modelo.Podcaster;

public class MusicoPodcasterTest {

    @Test
    public void testMusicoComposicion() {
        Musico m = new Musico(1, "Artist", "Rock", "Desc", "foto.png", "Grupo");
        m.setComposicion("Solista");
        assertEquals("Solista", m.getComposicion());
        assertTrue(m.toString().contains("composicion"));
    }

    @Test
    public void testPodcasterToString() {
        Podcaster p = new Podcaster(2, "Pod", "Talk", "Desc", "foto.png");
        assertTrue(p.toString().contains("Podcaster"));
    }
}
