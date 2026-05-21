package panel;

import controlador.GestorClienteNuevo;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import vista.VentanaPrincipal;

/**
 * Panel de inicio de sesión: formulario para que el usuario inicie
 * sesión o navegue al registro. Limpia campos al refrescar.
 */
public class PanelLogin extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorClienteNuevo gestor;
	private final JTextField txtUsuario;
	private final JPasswordField txtContrasena;

	/**
	 * Panel de login: prepara campos y botones para iniciar sesión.
	 *
	 * @param ventana ventana principal para navegación y acceso al gestor
	 */
	public PanelLogin(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.txtUsuario = new JTextField(20);
		this.txtContrasena = new JPasswordField(20);

		setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblTitulo = new JLabel("Spotify", SwingConstants.CENTER);
		lblTitulo.setFont(lblTitulo.getFont().deriveFont(26f));

		JLabel lblUsuario = new JLabel("Usuario");
		JLabel lblContrasena = new JLabel("Contraseña");
		JButton btnLogin = new JButton("Iniciar sesión");
		JButton btnRegistro = new JButton("Registrarse");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(lblTitulo, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		add(lblUsuario, gbc);
		gbc.gridy = 2;
		add(lblContrasena, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		add(txtUsuario, gbc);
		gbc.gridy = 2;
		add(txtContrasena, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		add(btnRegistro, gbc);
		gbc.gridx = 1;
		add(btnLogin, gbc);

		btnLogin.addActionListener(e -> iniciarSesion());
		btnRegistro.addActionListener(e -> ventana.cambiarPanel("registro"));
	}

	private void iniciarSesion() {
		String usuario = txtUsuario.getText().trim();
		String contrasena = new String(txtContrasena.getPassword()).trim();

		if (usuario.isEmpty() || contrasena.isEmpty()) {
			ventana.mostrarMensaje("Debes completar usuario y contraseña.");
			return;
		}

		var cliente = gestor.login(usuario, contrasena);
		if (cliente == null) {
			ventana.mostrarMensaje("Usuario o contraseña incorrectos.");
			return;
		}

		ventana.setClienteLogueado(cliente);
		txtContrasena.setText("");
		ventana.cambiarPanel(gestor.esAdmin(cliente) ? "admin" : "menuCliente");
	}

	@Override
	public void refrescar() {
		txtUsuario.setText("");
		txtContrasena.setText("");
	}
}
