package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.Cliente;
import vista.VentanaPrincipal;

/**
 * Panel para gestionar la actualización a cuenta Premium. Muestra
 * estado de la cuenta y permite solicitar la actualización.
 */
public class PanelPremium extends JPanel implements PanelRefrescable {

	private final VentanaPrincipal ventana;
	private final GestorClienteNuevo gestor;
	private final JLabel lblEstado;

	/**
	 * Panel para gestionar la actualización a Premium.
	 *
	 * @param ventana ventana principal para cambiar paneles y acceder al gestor
	 */
	public PanelPremium(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.gestor = ventana.getGestorCliente();
		this.lblEstado = new JLabel("", SwingConstants.CENTER);

		setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		setLayout(new BorderLayout(12, 12));

		JButton btnActualizar = new JButton("Actualizar ahora");
		JButton btnVolver = new JButton("Volver");

		JPanel centro = new JPanel(new BorderLayout(8, 8));
		centro.add(lblEstado, BorderLayout.CENTER);
		centro.add(btnActualizar, BorderLayout.SOUTH);

		add(btnVolver, BorderLayout.NORTH);
		add(centro, BorderLayout.CENTER);

		btnActualizar.addActionListener(e -> {
			Cliente cliente = ventana.getClienteLogueado();
			if (cliente == null) {
				ventana.mostrarMensaje("Debes iniciar sesión.");
				return;
			}
			if (cliente.isEsPremium()) {
				ventana.mostrarMensaje("Ya eres usuario Premium.");
				return;
			}
			if (gestor.actualizarAPremium()) {
				ventana.setClienteLogueado(gestor.getClienteActual());
				refrescar();
				ventana.mostrarMensaje("Ahora eres Premium.");
			} else {
				ventana.mostrarMensaje("No se pudo actualizar a Premium.");
			}
		});
		btnVolver.addActionListener(e -> ventana.cambiarPanel("menuCliente"));
	}

	@Override
	public void refrescar() {
		Cliente cliente = ventana.getClienteLogueado();
		if (cliente == null) {
			lblEstado.setText("Inicia sesión para ver el estado de tu cuenta.");
			return;
		}
		lblEstado.setText(cliente.isEsPremium() ? "Tu cuenta ya es Premium." : "Tu cuenta es Free. Puedes actualizarla a Premium.");
	}
}