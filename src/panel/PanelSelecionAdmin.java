package panel;

import javax.swing.JButton;
import javax.swing.JPanel;

import vista.VentanaPrincipal;
import controlador.GestorClienteNuevo;
import modelo.Cliente;

/**
 * Panel de selección de administración (equivalente del ejemplo proporcionado).
 */
public class PanelSelecionAdmin extends JPanel {
    private static final long serialVersionUID = 1L;

    public PanelSelecionAdmin(VentanaPrincipal ventana) {
        // Obtener referencias si se necesitan
        GestorClienteNuevo gestor = ventana.getGestorCliente();
        Cliente clienteLogueado = ventana.getClienteLogueado();

        setLayout(null);

        JButton btnAtras = new JButton("Atrás");
        btnAtras.setBounds(710, 10, 80, 30);

        JButton btnGestionMusicos = new JButton("Gestión de músicos");
        JButton btnGestionPodcasters = new JButton("Gestión de podcasters");
        JButton btnGestionAlbum = new JButton("Gestión de álbum");
        JButton btnGestionCanciones = new JButton("Gestión de canciones");
        JButton btnGestionPodcast = new JButton("Gestión de podcast");
        JButton btnEstatistica = new JButton("Estadísticas");

        btnGestionMusicos.setBounds(150, 150, 500, 50);
        btnGestionPodcasters.setBounds(150, 230, 500, 50);
        btnGestionAlbum.setBounds(150, 310, 500, 50);
        btnGestionCanciones.setBounds(150, 390, 500, 50);
        btnGestionPodcast.setBounds(150, 470, 500, 50);
        btnEstatistica.setBounds(150, 550, 500, 50);

        this.add(btnAtras);
        this.add(btnGestionMusicos);
        this.add(btnGestionPodcasters);
        this.add(btnGestionAlbum);
        this.add(btnGestionPodcast);
        this.add(btnGestionCanciones);
        this.add(btnEstatistica);

        // Listeners: cambiar panel en la ventana principal
        btnAtras.addActionListener(e -> ventana.cambiarPanel("login"));
        btnGestionMusicos.addActionListener(e -> ventana.cambiarPanel("GestionMusicos"));
        btnGestionPodcasters.addActionListener(e -> ventana.cambiarPanel("GestionPodcasters"));
        btnGestionAlbum.addActionListener(e -> ventana.cambiarPanel("GestionAlbum"));
        btnGestionCanciones.addActionListener(e -> ventana.cambiarPanel("GestionCanciones"));
        btnGestionPodcast.addActionListener(e -> ventana.cambiarPanel("GestionPodcast"));
        btnEstatistica.addActionListener(e -> ventana.cambiarPanel("StatAudio"));
    }
}
