package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Playlist;
import modelo.Cancion;

public class PlaylistTest {

    @Test
    public void testAddRemoveContainsAndDuration() {
        Playlist p = new Playlist(1, "Mix", "2026-01-01", 10);
        Cancion a = new Cancion(11, "One", "a.mp3", 60, 0, 0, "", "cancion");
        Cancion b = new Cancion(12, "Two", "b.mp3", 120, 0, 0, "", "cancion");

        assertTrue(p.addCancion(a));
        assertTrue(p.containsCancion(11));
        assertFalse(p.addCancion(a)); // duplicate id prevented

        assertTrue(p.addCancion(b));
        assertEquals(180, p.obtenerDuracionTotalSegundos());

        assertTrue(p.removeCancionById(11));
        assertFalse(p.containsCancion(11));
    }

    @Test
    public void testEqualsAndHashCode() {
        Playlist p1 = new Playlist(2, "A", "2026-01-01", 5);
        Playlist p2 = new Playlist(2, "B", "2026-02-02", 6);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
