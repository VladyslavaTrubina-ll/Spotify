package testear;

import static org.junit.Assert.*;
import org.junit.Test;
import modelo.Cliente;

public class ClienteTest {

    @Test
    public void testSetEsPremiumAjustaLimites() {
        Cliente c = new Cliente();
        c.setEsPremium(false);
        assertEquals(3, c.getLimitesPlaylists());

        c.setEsPremium(true);
        assertEquals(Integer.MAX_VALUE, c.getLimitesPlaylists());
    }

    @Test
    public void testNombreApellidoYUsuario() {
        Cliente c = new Cliente();
        c.setNombre("Ana");
        c.setApellido("Lopez");
        c.setUsuario("alopez");

        assertEquals("Ana", c.getNombre());
        assertEquals("Lopez", c.getApellido());
        assertEquals("alopez", c.getUsuario());
    }
}
