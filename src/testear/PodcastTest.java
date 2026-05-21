package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Podcast;

public class PodcastTest {

    @Test
    public void testGettersAndSetters() {
        Podcast p = new Podcast(5, "Talk", "t.mp3", 3600, 10, 7, 3, "podcast");
        assertEquals(7, p.getIdPodcaster());
        assertEquals(3, p.getNumeroParticipantes());

        p.setIdPodcaster(9);
        p.setNumeroParticipantes(5);
        assertEquals(9, p.getIdPodcaster());
        assertEquals(5, p.getNumeroParticipantes());
    }
}
