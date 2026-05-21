package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Cancion;

public class AudioTest {

    @Test
    public void testDuracionConvertidaYClamp() {
        // 125 seconds -> 2:05
        Cancion c = new Cancion(1, "Tema", "f.mp3", 125, 0, 0, "", "cancion");
        assertEquals("2:05", c.duracionConvertida());

        // negative duration should be clamped to 0
        Cancion c2 = new Cancion(2, "Tema2", "f2.mp3", -50, 0, 0, "", "cancion");
        assertEquals(0, c2.getDuracionSegundos());
    }
}
