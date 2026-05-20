package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.swing.*;
import modelo.Cancion;
import vista.VentanaPrincipal;
import controlador.ReproductorAudio;

public class PanelReproductor extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final DefaultListModel<Cancion> modeloCanciones;
	private final JList<Cancion> listaCanciones;
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

		setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
		setLayout(new BorderLayout(12, 12));

		lblImagen.setPreferredSize(new Dimension(320, 320));
		lblImagen.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaCanciones.setVisibleRowCount(8);
		cargarCanciones();

		JPanel izquierda = new JPanel(new BorderLayout(8, 8));
		izquierda.add(new JScrollPane(listaCanciones), BorderLayout.CENTER);
		JButton btnVolver = new JButton("Volver");
		izquierda.add(btnVolver, BorderLayout.SOUTH);

		JPanel centro = new JPanel(new BorderLayout(8, 8));
		centro.add(lblImagen, BorderLayout.CENTER);
		centro.add(lblTitulo, BorderLayout.NORTH);
		centro.add(lblProgreso, BorderLayout.SOUTH);

		JPanel controles = new JPanel(new GridLayout(2, 4, 8, 8));
		JButton btnAnterior = new JButton("Anterior");
		JButton btnPlay = new JButton("Play");
		JButton btnPausa = new JButton("Pausa");
		JButton btnSiguiente = new JButton("Siguiente");
		JButton btnExportar = new JButton("Exportar info");
		JButton btnFavorito = new JButton("Favorito");

		controles.add(btnAnterior);
		controles.add(btnPlay);
		controles.add(btnPausa);
		controles.add(btnSiguiente);
		controles.add(cmbVelocidad);
		controles.add(btnFavorito);
		controles.add(btnExportar);
		controles.add(lblEstado);

		txtDetalle.setEditable(false);
		txtDetalle.setLineWrap(true);
		txtDetalle.setWrapStyleWord(true);

		JPanel derecha = new JPanel(new BorderLayout(8, 8));
		derecha.add(controles, BorderLayout.NORTH);
		derecha.add(sliderProgreso, BorderLayout.CENTER);
		derecha.add(new JScrollPane(txtDetalle), BorderLayout.SOUTH);

		add(izquierda, BorderLayout.WEST);
		add(centro, BorderLayout.CENTER);
		add(derecha, BorderLayout.EAST);

		listaCanciones.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int seleccion = listaCanciones.getSelectedIndex();
				if (seleccion >= 0) {
					seleccionarCancion(seleccion, false);
				}
			}
		});

		btnAnterior.addActionListener(e -> anterior());
		btnPlay.addActionListener(e -> play());
		btnPausa.addActionListener(e -> pausar());
		btnSiguiente.addActionListener(e -> siguiente());
		btnExportar.addActionListener(e -> exportar());
		btnFavorito.addActionListener(e -> alternarFavorito());
		btnVolver.addActionListener(e -> {
			pausar();
			ventana.cambiarPanel("menuCliente");
		});

		cmbVelocidad.addActionListener(e -> aplicarVelocidad());
		sliderProgreso.setEnabled(false);

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
			modeloCanciones.addElement(cancion);
		}
	}

	private void seleccionarCancion(int indice, boolean autoPlay) {
		if (indice < 0 || indice >= modeloCanciones.size()) {
			return;
		}
		indiceActual = indice;
		Cancion cancion = modeloCanciones.getElementAt(indiceActual);
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
		if (modeloCanciones.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay canciones para reproducir");
			return;
		}

		try {
			if (clip != null && clip.isOpen()) {
				clip.stop();
				clip.close();
			}

			Cancion cancion = modeloCanciones.getElementAt(indiceActual);
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(cancion.getArchivo())));
			aplicarVelocidad();
			clip.start();
			reproduciendo = true;
			lblEstado.setText("Reproduciendo");
			iniciarActualizacionUI();
			lblProgreso.setText("00:00 / " + ReproductorAudio.formatearTiempo(cancion.getDuratasecondi()));
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
		if (modeloCanciones.isEmpty()) {
			return;
		}
		indiceActual = (indiceActual + 1) % modeloCanciones.size();
		listaCanciones.setSelectedIndex(indiceActual);
		seleccionarCancion(indiceActual, true);
	}

	private void anterior() {
		if (modeloCanciones.isEmpty()) {
			return;
		}
		indiceActual = (indiceActual - 1 + modeloCanciones.size()) % modeloCanciones.size();
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
			return;
		}
		JOptionPane.showMessageDialog(this,
				"Canción: " + cancion.getNombreAudio() + "\nArchivo: " + cancion.getArchivo() + "\nImagen: " + cancion.getFoto());
	}

	private Cancion obtenerCancionActual() {
		if (modeloCanciones.isEmpty() || indiceActual < 0 || indiceActual >= modeloCanciones.size()) {
			return null;
		}
		return modeloCanciones.getElementAt(indiceActual);
	}

	private void actualizarDetalle(Cancion cancion) {
		txtDetalle.setText("Título: " + cancion.getNombreAudio() + "\n" +
				"Audio: " + cancion.getArchivo() + "\n" +
				"Imagen: " + cancion.getFoto() + "\n" +
				"Duración: " + cancion.getDuratasecondi() + "\n" +
				"Reproduciendo: " + reproduciendo);
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
			int total = cancion.getDuratasecondi();
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