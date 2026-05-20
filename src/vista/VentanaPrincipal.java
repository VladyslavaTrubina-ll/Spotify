package vista;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import modelo.*;
import panel.*;
import controlador.GestorCliente;

public class VentanaPrincipal extends JFrame {

	private final GestorCliente gestorCliente;
	private final CardLayout cardLayout;
	private final JPanel panelContenedor;
	private final Map<String, String> aliasPaneles;
	private Cliente clienteLogueado;
	private Musico musicoSeleccionado;
	private Podcaster podcasterSeleccionado;
	private Album albumSeleccionado;
	private Podcast podcastSeleccionado;
	private Playlist playlistSeleccionada;
	private Audio audioSeleccionado;

	public VentanaPrincipal() {
		this("spoty");
	}

	public VentanaPrincipal(String nombreBaseDatos) {
		super("Spotify");
		this.gestorCliente = new GestorCliente(nombreBaseDatos);
		this.cardLayout = new CardLayout();
		this.panelContenedor = new JPanel(cardLayout);
		this.aliasPaneles = new HashMap<>();
		this.clienteLogueado = null;
		this.musicoSeleccionado = null;
		this.podcasterSeleccionado = null;
		this.albumSeleccionado = null;
		this.podcastSeleccionado = null;
		this.playlistSeleccionada = null;
		this.audioSeleccionado = null;

		configurarVentana();
		registrarAliasPaneles();
		registrarPaneles();
		cambiarPanel("login");
	}

	private void configurarVentana() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1000, 680);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		add(panelContenedor, BorderLayout.CENTER);
	}

	private void registrarAliasPaneles() {
		aliasPaneles.put("cliente", "menuCliente");
		aliasPaneles.put("panelAdmin", "admin");
		aliasPaneles.put("GestionMusicos", "admin");
		aliasPaneles.put("GestionPodcasters", "admin");
		aliasPaneles.put("GestionAlbum", "admin");
		aliasPaneles.put("GestionPodcast", "admin");
		aliasPaneles.put("GestionCanciones", "admin");
		aliasPaneles.put("StatCancion", "admin");
		aliasPaneles.put("StatAudio", "admin");
		aliasPaneles.put("StatPlaylist", "admin");
		aliasPaneles.put("StatPodcast", "admin");
	}

	private void registrarPaneles() {
		registrarPanel("login", new PanelLogin(this));
		registrarPanel("registro", new PanelRegistro(this));
		registrarPanel("menuCliente", new PanelMenuCliente(this));
		registrarPanel("musica", new PanelMusica(this));
		registrarPanel("podcasts", new PanelPodcasts(this));
		registrarPanel("favoritos", new PanelFavoritos(this));
		registrarPanel("playlists", new PanelPlaylists(this));
		registrarPanel("reproductor", new PanelReproductor(this));
		registrarPanel("premium", new PanelPremium(this));
		registrarPanel("admin", new PanelAdmin(this));
	}

	private void registrarPanel(String nombre, JPanel panel) {
		panelContenedor.add(panel, nombre);
	}

	public void cambiarPanel(String nombre) {
		String panelNormalizado = aliasPaneles.getOrDefault(nombre, nombre);
		cardLayout.show(panelContenedor, panelNormalizado);
		for (java.awt.Component componente : panelContenedor.getComponents()) {
			if (componente.isVisible() && componente instanceof PanelRefrescable) {
				((PanelRefrescable) componente).refrescar();
			}
		}
	}

	public GestorCliente getGestorCliente() {
		return gestorCliente;
	}

	public Cliente getClienteLogueado() {
		return clienteLogueado;
	}

	public void setClienteLogueado(Cliente clienteLogueado) {
		this.clienteLogueado = clienteLogueado;
	}

	public Musico getMusicoSeleccionado() {
		return musicoSeleccionado;
	}

	public void setMusicoSeleccionado(Musico musicoSeleccionado) {
		this.musicoSeleccionado = musicoSeleccionado;
	}

	public Podcaster getPodcasterSeleccionado() {
		return podcasterSeleccionado;
	}

	public void setPodcasterSeleccionado(Podcaster podcasterSeleccionado) {
		this.podcasterSeleccionado = podcasterSeleccionado;
	}

	public Album getAlbumSeleccionado() {
		return albumSeleccionado;
	}

	public void setAlbumSeleccionado(Album albumSeleccionado) {
		this.albumSeleccionado = albumSeleccionado;
	}

	public Podcast getPodcastSeleccionado() {
		return podcastSeleccionado;
	}

	public void setPodcastSeleccionado(Podcast podcastSeleccionado) {
		this.podcastSeleccionado = podcastSeleccionado;
	}

	public Playlist getPlaylistSeleccionada() {
		return playlistSeleccionada;
	}

	public void setPlaylistSeleccionada(Playlist playlistSeleccionada) {
		this.playlistSeleccionada = playlistSeleccionada;
	}

	public Audio getAudioSeleccionado() {
		return audioSeleccionado;
	}

	public void setAudioSeleccionado(Audio audioSeleccionado) {
		this.audioSeleccionado = audioSeleccionado;
	}

	public void mostrarMensaje(String mensaje) {
		javax.swing.JOptionPane.showMessageDialog(this, mensaje);
	}

	public void cerrarSesion() {
		gestorCliente.logout();
		clienteLogueado = null;
		cambiarPanel("login");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
	}

	public void ejecutarVentana() {
		setVisible(true);
	}
}