package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Album;
import modelo.Cancion;
import vista.VentanaPrincipal;

/**
 * Panel para explorar música: muestra artistas, álbumes y canciones.
 * Permite navegar por discografías y reproducir/añadir contenidos.
 */
public class PanelMusica extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorClienteNuevo gestor;
	private final JComboBox<String> cmbArtistas;
	private final DefaultListModel<Album> modeloAlbums;
	private final DefaultListModel<Cancion> modeloCanciones;
	private final JTextArea txtResumen;

	/**
	 * Inicializa el panel de exploración musical (artistas, álbumes y canciones).
	 *
	 * @param ventana ventana principal para acceder al gestor y navegación
	 */
	public PanelMusica(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.cmbArtistas = new JComboBox<>();
		this.modeloAlbums = new DefaultListModel<>();
		this.modeloCanciones = new DefaultListModel<>();
		this.txtResumen = new JTextArea(5, 20);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout(12, 12));

		JPanel superior = new JPanel(new BorderLayout(8, 8));
		superior.add(cmbArtistas, BorderLayout.CENTER);
		JButton btnVolver = new JButton("Volver");
		JButton btnActualizar = new JButton("Actualizar");
		superior.add(btnActualizar, BorderLayout.EAST);
		superior.add(btnVolver, BorderLayout.WEST);

		JList<Album> listaAlbums = new JList<>(modeloAlbums);
		JList<Cancion> listaCanciones = new JList<>(modeloCanciones);

		JPanel centro = new JPanel(new GridLayout(1, 2, 12, 12));
		centro.add(new JScrollPane(listaAlbums));
		centro.add(new JScrollPane(listaCanciones));

		txtResumen.setEditable(false);
		add(superior, BorderLayout.NORTH);
		add(centro, BorderLayout.CENTER);
		add(new JScrollPane(txtResumen), BorderLayout.SOUTH);

		cmbArtistas.addActionListener(e -> cargarAlbumsYCanciones());
		listaAlbums.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Album album = listaAlbums.getSelectedValue();
				if (album != null) {
					cargarCanciones(album.getTitulo());
				}
			}
		});
		listaCanciones.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Cancion cancion = listaCanciones.getSelectedValue();
				if (cancion != null) {
					txtResumen.setText("Canción seleccionada: " + cancion.getNombreAudio() + "\nDuración: " + cancion.duracionConvertida());
				}
			}
		});
		btnActualizar.addActionListener(e -> refrescar());
		btnVolver.addActionListener(e -> ventana.cambiarPanel("menuCliente"));
	}

	private void cargarAlbumsYCanciones() {
		modeloAlbums.clear();
		modeloCanciones.clear();
		String artista = (String) cmbArtistas.getSelectedItem();
		if (artista == null || artista.isBlank()) {
			txtResumen.setText("No hay artistas disponibles.");
			return;
		}

		ArrayList<Album> albums = gestor.obtenerDiscografia(artista);
		for (Album album : albums) {
			modeloAlbums.addElement(album);
		}
		txtResumen.setText("Artista: " + artista + "\nÁlbumes encontrados: " + albums.size());
		if (!albums.isEmpty()) {
			cargarCanciones(albums.get(0).getTitulo());
		}
	}

	private void cargarCanciones(String nombreAlbum) {
		modeloCanciones.clear();
		ArrayList<Cancion> canciones = gestor.obtenerCancionesAlbum(nombreAlbum);
		for (Cancion cancion : canciones) {
			modeloCanciones.addElement(cancion);
		}
		txtResumen.setText("Álbum: " + nombreAlbum + "\nCanciones encontradas: " + canciones.size());
	}

	@Override
	public void refrescar() {
		cmbArtistas.removeAllItems();
		for (String artista : gestor.obtenerArtistas()) {
			cmbArtistas.addItem(artista);
		}
		if (cmbArtistas.getItemCount() > 0) {
			cmbArtistas.setSelectedIndex(0);
			cargarAlbumsYCanciones();
		} else {
			modeloAlbums.clear();
			modeloCanciones.clear();
			txtResumen.setText("No hay artistas cargados.");
		}
	}
}