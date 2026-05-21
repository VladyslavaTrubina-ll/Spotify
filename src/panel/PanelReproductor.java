package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.Cancion;
import vista.VentanaPrincipal;
import controlador.ReproductorAudio;

public class PanelReproductor extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final DefaultListModel<String> modeloCanciones;
	private final JList<String> listaCanciones;
	private final JLabel lblImagen;
	private final JLabel lblTitulo;
	private final JLabel lblProgreso;
	private final JLabel lblEstado;
	private final JComboBox<String> cmbVelocidad;
	private final JSlider sliderProgreso;
	private final JTextArea txtDetalle;
	private final ArrayList<Cancion> cancionesDemo;
	private Clip clip;
	private int indiceActual;
	private boolean reproduciendo;
	private Timer timerUI;

	public PanelReproductor(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.modeloCanciones = new DefaultListModel<>();
		this.listaCanciones = new JList<>(modeloCanciones);
		this.lblImagen = new JLabel();
		this.lblTitulo = new JLabel("Selecciona una canción", SwingConstants.CENTER);
		this.lblProgreso = new JLabel("00:00 / 00:00", SwingConstants.CENTER);
		this.lblEstado = new JLabel("Detenido", SwingConstants.CENTER);
		this.cmbVelocidad = new JComboBox<>(new String[] {"0.5x", "1.0x", "1.5x", "2.0x"});
		this.sliderProgreso = new JSlider(0, 100, 0);
		this.txtDetalle = new JTextArea(5, 20);
		this.cancionesDemo = crearCancionesDemo();
		this.clip = null;
		this.indiceActual = 0;
		this.reproduciendo = false;
		this.timerUI = null;

		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// ================= TOP PANEL =================
		JPanel top = new JPanel(new BorderLayout());
		JButton btnVolver = new JButton("Volver");
		btnVolver.addActionListener(e -> {
			pausar();
			ventana.cambiarPanel("menuCliente");
		});
		top.add(btnVolver, BorderLayout.WEST);
		add(top, BorderLayout.NORTH);

		// ================= CENTER PANEL =================
		JPanel center = new JPanel(new BorderLayout(15, 15));

		// ================= LISTA CANCIONES =================
		JPanel panelLista = new JPanel(new BorderLayout());
		panelLista.setBorder(BorderFactory.createTitledBorder("Canciones"));

		listaCanciones.setFont(new Font("Arial", Font.BOLD, 16));
		listaCanciones.setFixedCellHeight(45);
		listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scroll = new JScrollPane(listaCanciones);
		panelLista.add(scroll, BorderLayout.CENTER);

		cargarCanciones();

		// DOBLE CLICK PARA REPRODUCIR
		listaCanciones.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = listaCanciones.getSelectedIndex();
					if (index >= 0) {
						seleccionarCancion(index, true);
					}
				}
			}
		});

		// SINGLE CLICK PARA SELECCIONAR
		listaCanciones.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int seleccion = listaCanciones.getSelectedIndex();
				if (seleccion >= 0) {
					seleccionarCancion(seleccion, false);
				}
			}
		});

		// ================= PANEL CENTRO (IMAGEN + INFO) =================
		JPanel panelCentro = new JPanel(new BorderLayout(8, 8));
		lblImagen.setPreferredSize(new Dimension(300, 300));
		lblImagen.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panelCentro.add(lblImagen, BorderLayout.CENTER);
		panelCentro.add(lblTitulo, BorderLayout.NORTH);
		panelCentro.add(lblProgreso, BorderLayout.SOUTH);

		// ================= PANEL BOTONES =================
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(3, 2, 10, 10));
		panelBotones.setPreferredSize(new Dimension(220, 300));

		JButton btnAnterior = new JButton("◄ Anterior");
		JButton btnPlay = new JButton("▶ Play");
		JButton btnPausa = new JButton("⏸ Pausa");
		JButton btnSiguiente = new JButton("Siguiente ►");
		JButton btnFavorito = new JButton("♥ Favorito");
		JButton btnExportar = new JButton("Exportar");

		Font fontBotones = new Font("Arial", Font.BOLD, 14);
		btnAnterior.setFont(fontBotones);
		btnPlay.setFont(fontBotones);
		btnPausa.setFont(fontBotones);
		btnSiguiente.setFont(fontBotones);
		btnFavorito.setFont(fontBotones);
		btnExportar.setFont(fontBotones);

		panelBotones.add(btnAnterior);
		panelBotones.add(btnSiguiente);
		panelBotones.add(btnPlay);
		panelBotones.add(btnPausa);
		panelBotones.add(btnFavorito);
		panelBotones.add(btnExportar);

		btnAnterior.addActionListener(e -> anterior());
		btnPlay.addActionListener(e -> play());
		btnPausa.addActionListener(e -> pausar());
		btnSiguiente.addActionListener(e -> siguiente());
		btnExportar.addActionListener(e -> exportar());
		btnFavorito.addActionListener(e -> alternarFavorito());

		// ================= PANEL DERECHO (CONTROLES + DETALLES) =================
		JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));
		
		JPanel panelControles = new JPanel(new GridLayout(2, 1, 8, 8));
		panelControles.add(new JLabel("Velocidad:"));
		panelControles.add(cmbVelocidad);
		cmbVelocidad.addActionListener(e -> aplicarVelocidad());

		txtDetalle.setEditable(false);
		txtDetalle.setLineWrap(true);
		txtDetalle.setWrapStyleWord(true);

		panelDerecho.add(panelControles, BorderLayout.NORTH);
		panelDerecho.add(sliderProgreso, BorderLayout.CENTER);
		panelDerecho.add(new JScrollPane(txtDetalle), BorderLayout.SOUTH);

		sliderProgreso.setEnabled(false);

		// LAYOUT PRINCIPAL
		center.add(panelLista, BorderLayout.WEST);
		center.add(panelCentro, BorderLayout.CENTER);
		center.add(panelDerecho, BorderLayout.EAST);

		JPanel panelSur = new JPanel(new BorderLayout());
		panelSur.add(lblEstado, BorderLayout.CENTER);
		panelSur.add(panelBotones, BorderLayout.EAST);

		add(center, BorderLayout.CENTER);
		add(panelSur, BorderLayout.SOUTH);

		if (modeloCanciones.getSize() > 0) {
			listaCanciones.setSelectedIndex(0);
			seleccionarCancion(0, false);
		}
	}

	private ArrayList<Cancion> crearCancionesDemo() {
		ArrayList<Cancion> demo = new ArrayList<>();
		demo.add(new Cancion(1, "Cancion 1", "canciones/cancion1.wav", 12, 0, 0, null, "Cancion", "imagenes/imagen1.png"));
		demo.add(new Cancion(2, "Cancion 2", "canciones/cancion2.wav", 12, 0, 0, null, "Cancion", "imagenes/imagen2.gif"));
		return demo;
	}

	private void cargarCanciones() {
		modeloCanciones.clear();
		for (Cancion cancion : cancionesDemo) {
			modeloCanciones.addElement(cancion.getNombreAudio() + " | " + cancion.getDuracionSegundos() + "s");
		}
	}

	private void seleccionarCancion(int indice, boolean autoPlay) {
		if (indice < 0 || indice >= cancionesDemo.size()) {
			return;
		}
		indiceActual = indice;
		Cancion cancion = cancionesDemo.get(indiceActual);
		lblTitulo.setText(cancion.getNombreAudio());
		lblImagen.setIcon(cargarImagen(cancion.getFoto()));
		lblEstado.setText(reproduciendo ? "Reproduciendo" : "Pausado");
		actualizarDetalle(cancion);
		if (autoPlay) {
			play();
		}
	}

	private ImageIcon cargarImagen(String ruta) {
		if (ruta == null || ruta.isBlank()) {
			return null;
		}
		File archivo = new File(ruta);
		if (!archivo.exists()) {
			return null;
		}
		return new ImageIcon(ruta);
	}

	private void play() {
		if (cancionesDemo.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay canciones para reproducir");
			return;
		}

		try {
			if (clip != null && clip.isOpen()) {
				clip.stop();
				clip.close();
			}

			Cancion cancion = cancionesDemo.get(indiceActual);
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(cancion.getArchivo())));
			aplicarVelocidad();
			clip.start();
			reproduciendo = true;
			lblEstado.setText("Reproduciendo");
			iniciarActualizacionUI();
			lblProgreso.setText("00:00 / " + ReproductorAudio.formatearTiempo(cancion.getDuracionSegundos()));
		} catch (LineUnavailableException | UnsupportedAudioFileException | java.io.IOException e) {
			JOptionPane.showMessageDialog(this, "No se pudo reproducir el archivo: " + e.getMessage());
		}
	}

	private void pausar() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			reproduciendo = false;
			lblEstado.setText("Pausado");
			pararActualizacionUI();
		}
	}

	private void siguiente() {
		if (cancionesDemo.isEmpty()) {
			return;
		}
		indiceActual = (indiceActual + 1) % cancionesDemo.size();
		listaCanciones.setSelectedIndex(indiceActual);
		seleccionarCancion(indiceActual, true);
	}

	private void anterior() {
		if (cancionesDemo.isEmpty()) {
			return;
		}
		indiceActual = (indiceActual - 1 + cancionesDemo.size()) % cancionesDemo.size();
		listaCanciones.setSelectedIndex(indiceActual);
		seleccionarCancion(indiceActual, true);
	}

	private void aplicarVelocidad() {
		if (clip == null || !clip.isOpen()) {
			return;
		}
		try {
			double velocidad = switch (cmbVelocidad.getSelectedIndex()) {
				case 0 -> 0.5;
				case 1 -> 1.0;
				case 2 -> 1.5;
				default -> 2.0;
			};

			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float dB = switch ((int) velocidad) {
				case 0 -> -10.0f;
				case 1 -> 0.0f;
				case 2 -> 3.0f;
				default -> 6.0f;
			};
			if (control != null) {
				control.setValue(Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB)));
			}
		} catch (Exception ignored) {
			// Algunos sistemas no exponen control de ganancia
		}
	}

	private void alternarFavorito() {
		Cancion cancion = obtenerCancionActual();
		if (cancion == null) {
			return;
		}
		boolean estaFavorito = ventana.getGestorCliente().estaEnFavoritosCliente(cancion.getId());
		boolean resultado = estaFavorito ? ventana.getGestorCliente().eliminarDeFavoritosCliente(cancion.getId())
				: ventana.getGestorCliente().agregarAFavoritosCliente(cancion.getId());
			
		if (resultado) {
			lblEstado.setText(estaFavorito ? "Eliminado de favoritos" : "Añadido a favoritos");
		} else {
			lblEstado.setText("No se pudo actualizar favoritos");
		}
	}

	private void exportar() {
		Cancion cancion = obtenerCancionActual();
		if (cancion == null) {
			JOptionPane.showMessageDialog(this, "Selecciona una canción para exportar.");
			return;
		}

		String nombreBase = cancion.getNombreAudio() == null || cancion.getNombreAudio().isBlank()
				? "cancion_exportada"
				: cancion.getNombreAudio().replaceAll("[\\\\/:*?\"<>|]", "_");

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Exportar información de la canción");
		chooser.setSelectedFile(new File(nombreBase + ".txt"));
		chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

		int resultado = chooser.showSaveDialog(this);
		if (resultado != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File archivo = chooser.getSelectedFile();
		if (!archivo.getName().toLowerCase().endsWith(".txt")) {
			archivo = new File(archivo.getParentFile(), archivo.getName() + ".txt");
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
			writer.write("=== Información de la canción ===");
			writer.newLine();
			writer.write("Nombre: " + cancion.getNombreAudio());
			writer.newLine();
			writer.write("Archivo: " + cancion.getArchivo());
			writer.newLine();
			writer.write("Imagen: " + cancion.getFoto());
			writer.newLine();
			writer.write("Duración: " + cancion.getDuracionSegundos() + "s");
			writer.newLine();
			writer.write("Reproducciones: " + cancion.getReproducciones());
			writer.newLine();
			writer.write("Estado: " + (reproduciendo ? "Reproduciendo" : "Pausado"));
			writer.newLine();

			JOptionPane.showMessageDialog(this,
					"Exportación completada correctamente.\n\nArchivo guardado en:\n" + archivo.getAbsolutePath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "No se pudo exportar la canción: " + e.getMessage());
		}
	}

	private Cancion obtenerCancionActual() {
		if (cancionesDemo.isEmpty() || indiceActual < 0 || indiceActual >= cancionesDemo.size()) {
			return null;
		}
		return cancionesDemo.get(indiceActual);
	}

	private void actualizarDetalle(Cancion cancion) {
		txtDetalle.setText("Título: " + cancion.getNombreAudio() + "\n" +
				"Audio: " + cancion.getArchivo() + "\n" +
				"Imagen: " + cancion.getFoto() + "\n" +
				"Duración: " + cancion.getDuracionSegundos() + "s\n" +
				"Estado: " + (reproduciendo ? "Reproduciendo" : "Pausado"));
	}

	private void iniciarActualizacionUI() {
		pararActualizacionUI();
		timerUI = new Timer(1000, e -> {
			Cancion cancion = obtenerCancionActual();
			if (cancion == null || clip == null) {
				return;
			}
			long microsegundos = clip.getMicrosecondPosition();
			int segundos = (int) (microsegundos / 1_000_000L);
			int total = cancion.getDuracionSegundos();
			if (total <= 0) {
				total = 1;
			}
			lblProgreso.setText(ReproductorAudio.formatearTiempo(segundos) + " / " + ReproductorAudio.formatearTiempo(total));
			sliderProgreso.setValue(Math.min(100, (int) ((segundos * 100.0) / total)));
		});
		timerUI.start();
	}

	private void pararActualizacionUI() {
		if (timerUI != null) {
			timerUI.stop();
			timerUI = null;
		}
	}

	@Override
	public void refrescar() {
		if (listaCanciones.getModel().getSize() > 0 && listaCanciones.getSelectedIndex() < 0) {
			listaCanciones.setSelectedIndex(0);
			seleccionarCancion(0, false);
		}
	}
}
