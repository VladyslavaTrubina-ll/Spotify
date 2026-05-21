package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import vista.VentanaPrincipal;

public class PanelAdmin extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorClienteNuevo gestor;
	private final JTextArea txtArea;

	public PanelAdmin(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.txtArea = new JTextArea();

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout(10, 10));

		JButton btnActualizar = new JButton("Actualizar estadísticas");
		JButton btnAdminMusica = new JButton("Gestionar Música");
		JButton btnAdminPodcasts = new JButton("Gestionar Podcasts");
		JButton btnVolver = new JButton("Cerrar sesión");
		
		JPanel superior = new JPanel(new GridLayout(1, 4, 10, 10));
		superior.add(btnAdminMusica);
		superior.add(btnAdminPodcasts);
		superior.add(btnActualizar);
		superior.add(btnVolver);

		txtArea.setEditable(false);
		add(superior, BorderLayout.NORTH);
		add(new JScrollPane(txtArea), BorderLayout.CENTER);

		btnActualizar.addActionListener(e -> refrescar());
		btnVolver.addActionListener(e -> ventana.cerrarSesion());
		btnAdminMusica.addActionListener(e -> ventana.cambiarPanel("adminMusica"));
		btnAdminPodcasts.addActionListener(e -> ventana.cambiarPanel("adminPodcasts"));
	}

	@Override
	public void refrescar() {
		StringBuilder sb = new StringBuilder();
		sb.append("Panel de administración\n\n");
		sb.append("=== ESTADÍSTICAS ===\n\n");
		sb.append("Canciones: ").append(gestor.obtenerEstadisticasCanciones()).append("\n\n");
		sb.append("Audios: ").append(gestor.obtenerEstadisticasAudio()).append("\n\n");
		sb.append("Podcasts: ").append(gestor.obtenerEstadisticasPodcast()).append("\n\n");
		sb.append("Playlists: ").append(gestor.obtenerEstadisticasPlaylist()).append("\n");
		txtArea.setText(sb.toString());
	}
}