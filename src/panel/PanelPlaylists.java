package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Playlist;
import vista.VentanaPrincipal;

public class PanelPlaylists extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorClienteNuevo gestor;
	private final DefaultListModel<Playlist> modeloPlaylists;
	private final JTextArea txtDetalle;

	public PanelPlaylists(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.modeloPlaylists = new DefaultListModel<>();
		this.txtDetalle = new JTextArea(5, 20);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout(12, 12));

		JList<Playlist> listaPlaylists = new JList<>(modeloPlaylists);
		JButton btnCrear = new JButton("Crear playlist");
		JButton btnEliminar = new JButton("Eliminar playlist");
		JButton btnActualizar = new JButton("Actualizar");
		JButton btnVolver = new JButton("Volver");

		JPanel superior = new JPanel(new BorderLayout(8, 8));
		superior.add(btnVolver, BorderLayout.WEST);
		superior.add(btnActualizar, BorderLayout.EAST);

		JPanel inferior = new JPanel(new BorderLayout(8, 8));
		inferior.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);
		inferior.add(btnEliminar, BorderLayout.EAST);

		JPanel acciones = new JPanel(new java.awt.GridLayout(1, 2, 10, 10));
		acciones.add(btnCrear);
		acciones.add(inferior);

		add(superior, BorderLayout.NORTH);
		add(new JScrollPane(listaPlaylists), BorderLayout.CENTER);
		add(acciones, BorderLayout.SOUTH);

		listaPlaylists.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Playlist playlist = listaPlaylists.getSelectedValue();
				if (playlist != null) {
					txtDetalle.setText("Playlist: " + playlist.getTitulo() + "\nCanciones: " + playlist.getCanciones().size() + "\nCreada: " + playlist.getFechaCreacion());
				}
			}
		});

		btnActualizar.addActionListener(e -> refrescar());
		btnCrear.addActionListener(e -> {
			String nombre = JOptionPane.showInputDialog(this, "Nombre de la playlist:");
			if (nombre == null || nombre.trim().isEmpty()) {
				return;
			}
			if (gestor.crearPlaylist(nombre.trim())) {
				refrescar();
			} else {
				ventana.mostrarMensaje("No se pudo crear la playlist.");
			}
		});
		btnEliminar.addActionListener(e -> {
			Playlist playlist = listaPlaylists.getSelectedValue();
			if (playlist == null) {
				ventana.mostrarMensaje("Selecciona una playlist.");
				return;
			}
			if (gestor.eliminarPlaylist(playlist.getTitulo())) {
				refrescar();
			} else {
				ventana.mostrarMensaje("No se pudo eliminar la playlist.");
			}
		});
		btnVolver.addActionListener(e -> ventana.cambiarPanel("menuCliente"));
	}

	@Override
	public void refrescar() {
		modeloPlaylists.clear();
		for (Playlist playlist : gestor.obtenerPlaylistsCliente()) {
			modeloPlaylists.addElement(playlist);
		}
		txtDetalle.setText("Playlists cargadas: " + modeloPlaylists.size());
	}
}