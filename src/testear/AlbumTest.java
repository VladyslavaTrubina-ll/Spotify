package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Album;

public class AlbumTest {

    @Test
    public void testConstructorAndAccessors() {
        Album a = new Album(3, "Hits", "2020", "Pop", "img.png", 2);
        assertEquals(3, a.getId());
        assertEquals("Hits", a.getTitulo());
        assertEquals("2020", a.getFechaPub());
        assertEquals("Pop", a.getGenero());
        assertEquals(2, a.getIdMusico());
        assertTrue(a.toString().contains("Hits"));
    }
}
