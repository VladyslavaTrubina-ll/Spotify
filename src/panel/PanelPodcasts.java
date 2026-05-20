package panel;

import controlador.GestorCliente;
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
import modelo.Podcast;
import modelo.Podcaster;
import vista.VentanaPrincipal;

public class PanelPodcasts extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorCliente gestor;
	private final JComboBox<String> cmbPodcasters;
	private final DefaultListModel<Podcast> modeloPodcasts;
	private final JTextArea txtResumen;

	public PanelPodcasts(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.cmbPodcasters = new JComboBox<>();
		this.modeloPodcasts = new DefaultListModel<>();
		this.txtResumen = new JTextArea(5, 20);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout(12, 12));

		JPanel superior = new JPanel(new BorderLayout(8, 8));
		superior.add(cmbPodcasters, BorderLayout.CENTER);
		JButton btnVolver = new JButton("Volver");
		JButton btnActualizar = new JButton("Actualizar");
		superior.add(btnActualizar, BorderLayout.EAST);
		superior.add(btnVolver, BorderLayout.WEST);

		JList<Podcast> listaPodcasts = new JList<>(modeloPodcasts);

		txtResumen.setEditable(false);
		add(superior, BorderLayout.NORTH);
		add(new JScrollPane(listaPodcasts), BorderLayout.CENTER);
		add(new JScrollPane(txtResumen), BorderLayout.SOUTH);

		cmbPodcasters.addActionListener(e -> cargarPodcasts());
		listaPodcasts.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Podcast podcast = listaPodcasts.getSelectedValue();
				if (podcast != null) {
					txtResumen.setText("Podcast seleccionado: " + podcast.getNombreAudio() + "\nParticipantes: " + podcast.getNumeroParticipantes());
				}
			}
		});
		btnActualizar.addActionListener(e -> refrescar());
		btnVolver.addActionListener(e -> ventana.cambiarPanel("menuCliente"));
	}

	private void cargarPodcasts() {
		modeloPodcasts.clear();
		String podcaster = (String) cmbPodcasters.getSelectedItem();
		if (podcaster == null || podcaster.isBlank()) {
			txtResumen.setText("No hay podcasters disponibles.");
			return;
		}

		ArrayList<Podcast> podcasts = gestor.obtenerPodcasts(podcaster);
		for (Podcast podcast : podcasts) {
			modeloPodcasts.addElement(podcast);
		}
		txtResumen.setText("Podcaster: " + podcaster + "\nPodcasts encontrados: " + podcasts.size());
	}

	@Override
	public void refrescar() {
		cmbPodcasters.removeAllItems();
		for (Podcaster podcaster : gestor.obtenerPodcasters()) {
			cmbPodcasters.addItem(podcaster.getNombreArt());
		}
		if (cmbPodcasters.getItemCount() > 0) {
			cmbPodcasters.setSelectedIndex(0);
			cargarPodcasts();
		} else {
			modeloPodcasts.clear();
			txtResumen.setText("No hay podcasts cargados.");
		}
	}
}