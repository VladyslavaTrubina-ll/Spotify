package panel;

import controlador.GestorCliente;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import vista.VentanaPrincipal;

public class PanelRegistro extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorCliente gestor;
	private final JTextField txtNombre;
	private final JTextField txtApellido;
	private final JTextField txtUsuario;
	private final JPasswordField txtContrasena;
	private final JTextField txtFechaNacimiento;
	private final JComboBox<String> cmbIdioma;

	public PanelRegistro(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.txtNombre = new JTextField(20);
		this.txtApellido = new JTextField(20);
		this.txtUsuario = new JTextField(20);
		this.txtContrasena = new JPasswordField(20);
		this.txtFechaNacimiento = new JTextField(20);
		this.cmbIdioma = new JComboBox<>(new String[] {"Español", "Inglés", "Francés", "Italiano"});

		setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblTitulo = new JLabel("Crear cuenta");
		lblTitulo.setFont(lblTitulo.getFont().deriveFont(24f));

		JButton btnVolver = new JButton("Volver");
		JButton btnRegistrar = new JButton("Registrar");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(lblTitulo, gbc);

		gbc.gridwidth = 1;
		addFila("Nombre", txtNombre, 1, gbc);
		addFila("Apellido", txtApellido, 2, gbc);
		addFila("Usuario", txtUsuario, 3, gbc);
		addFila("Contraseña", txtContrasena, 4, gbc);
		addFila("Fecha de nacimiento (YYYY-MM-DD)", txtFechaNacimiento, 5, gbc);
		addFila("Idioma", cmbIdioma, 6, gbc);

		gbc.gridx = 0;
		gbc.gridy = 7;
		add(btnVolver, gbc);
		gbc.gridx = 1;
		add(btnRegistrar, gbc);

		btnVolver.addActionListener(e -> ventana.cambiarPanel("login"));
		btnRegistrar.addActionListener(e -> registrar());
	}

	private void addFila(String texto, java.awt.Component campo, int fila, GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = fila;
		add(new JLabel(texto), gbc);
		gbc.gridx = 1;
		add(campo, gbc);
	}

	private void registrar() {
		String nombre = txtNombre.getText().trim();
		String apellido = txtApellido.getText().trim();
		String usuario = txtUsuario.getText().trim();
		String contrasena = new String(txtContrasena.getPassword()).trim();
		String fechaNacimiento = txtFechaNacimiento.getText().trim();
		String idioma = (String) cmbIdioma.getSelectedItem();

		if (nombre.isEmpty() || apellido.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || fechaNacimiento.isEmpty()) {
			ventana.mostrarMensaje("Completa todos los campos.");
			return;
		}

		boolean registrado = gestor.registrarCliente(nombre, apellido, usuario, contrasena, fechaNacimiento, idioma);
		if (registrado) {
			ventana.mostrarMensaje("Registro completado. Ya puedes iniciar sesión.");
			limpiar();
			ventana.cambiarPanel("login");
			return;
		}

		ventana.mostrarMensaje("No se pudo registrar el cliente.");
	}

	private void limpiar() {
		txtNombre.setText("");
		txtApellido.setText("");
		txtUsuario.setText("");
		txtContrasena.setText("");
		txtFechaNacimiento.setText("");
		cmbIdioma.setSelectedIndex(0);
	}

	@Override
	public void refrescar() {
		limpiar();
	}
}