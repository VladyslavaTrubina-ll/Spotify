package panel;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.Cliente;
import vista.VentanaPrincipal;

/**
 * Panel principal del cliente que ofrece navegación hacia música,
 * podcasts, favoritos, playlists y opciones de cuenta.
 */
public class PanelMenuCliente extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final JLabel lblBienvenida;

	/**
	 * Panel de navegación principal para clientes autenticados.
	 *
	 * @param ventana ventana principal para cambiar paneles y acceder al gestor
	 */
	public PanelMenuCliente(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.lblBienvenida = new JLabel("", SwingConstants.CENTER);

		setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		setLayout(new GridLayout(0, 1, 12, 12));

		lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(22f));

		JButton btnMusica = new JButton("Explorar música");
		JButton btnPodcasts = new JButton("Explorar podcasts");
		JButton btnFavoritos = new JButton("Mis favoritos");
		JButton btnPlaylists = new JButton("Mis playlists");
		JButton btnReproductor = new JButton("Reproductor");
		JButton btnPremium = new JButton("Actualizar a Premium");
		JButton btnSalir = new JButton("Cerrar sesión");

		add(lblBienvenida);
		add(btnMusica);
		add(btnPodcasts);
		add(btnFavoritos);
		add(btnPlaylists);
		add(btnReproductor);
		add(btnPremium);
		add(btnSalir);

		btnMusica.addActionListener(e -> ventana.cambiarPanel("musica"));
		btnPodcasts.addActionListener(e -> ventana.cambiarPanel("podcasts"));
		btnFavoritos.addActionListener(e -> ventana.cambiarPanel("favoritos"));
		btnPlaylists.addActionListener(e -> ventana.cambiarPanel("playlists"));
		btnReproductor.addActionListener(e -> ventana.cambiarPanel("reproductor"));
		btnPremium.addActionListener(e -> ventana.cambiarPanel("premium"));
		btnSalir.addActionListener(e -> ventana.cerrarSesion());
	}

	@Override
	public void refrescar() {
		Cliente cliente = ventana.getClienteLogueado();
		if (cliente == null) {
			lblBienvenida.setText("Bienvenido");
			return;
		}
		lblBienvenida.setText("Hola, " + cliente.getNombre() + " " + cliente.getApellido() + " - " + (cliente.isEsPremium() ? "Premium" : "Free"));
	}
}