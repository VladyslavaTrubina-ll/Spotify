package panel;

import controlador.GestorCliente;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Audio;
import vista.VentanaPrincipal;

public class PanelFavoritos extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorCliente gestor;
	private final DefaultListModel<Audio> modeloFavoritos;
	private final JTextArea txtDetalle;

	public PanelFavoritos(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.modeloFavoritos = new DefaultListModel<>();
		this.txtDetalle = new JTextArea(5, 20);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout(12, 12));

		JList<Audio> listaFavoritos = new JList<>(modeloFavoritos);
		JButton btnActualizar = new JButton("Actualizar");
		JButton btnEliminar = new JButton("Quitar de favoritos");
		JButton btnVolver = new JButton("Volver");

		JPanel superior = new JPanel(new BorderLayout(8, 8));
		superior.add(btnVolver, BorderLayout.WEST);
		superior.add(btnActualizar, BorderLayout.EAST);

		JPanel inferior = new JPanel(new BorderLayout(8, 8));
		inferior.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);
		inferior.add(btnEliminar, BorderLayout.EAST);

		JPanel acciones = new JPanel(new java.awt.GridLayout(1, 1, 10, 10));
		acciones.add(inferior);

		add(superior, BorderLayout.NORTH);
		add(new JScrollPane(listaFavoritos), BorderLayout.CENTER);
		add(acciones, BorderLayout.SOUTH);

		listaFavoritos.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Audio audio = listaFavoritos.getSelectedValue();
				if (audio != null) {
					txtDetalle.setText("Audio: " + audio.getNombreAudio() + "\nID: " + audio.getId() + "\nDuración: " + audio.durataConvertida());
				}
			}
		});

		btnActualizar.addActionListener(e -> refrescar());
		btnEliminar.addActionListener(e -> {
			Audio audio = listaFavoritos.getSelectedValue();
			if (audio == null) {
				ventana.mostrarMensaje("Selecciona un favorito para quitarlo.");
				return;
			}
			if (gestor.eliminarDeFavoritosCliente(audio.getId())) {
				refrescar();
			}
		});
		btnVolver.addActionListener(e -> ventana.cambiarPanel("menuCliente"));
	}

	@Override
	public void refrescar() {
		modeloFavoritos.clear();
		for (Audio audio : gestor.obtenerFavoritosCliente()) {
			modeloFavoritos.addElement(audio);
		}
		txtDetalle.setText("Favoritos cargados: " + modeloFavoritos.size());
	}
}