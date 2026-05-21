package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import modelo.Podcast;
import modelo.Podcaster;
import vista.VentanaPrincipal;

/**
 * Panel de administración para gestionar podcasters y podcasts
 */
public class PanelAdminPodcasts extends JPanel implements PanelRefrescable {

    private final VentanaPrincipal ventana;
    private final GestorClienteNuevo gestor;

    // Componentes para Podcasters
    private DefaultListModel<Podcaster> modeloPodcasters;
    private JList<Podcaster> listaPodcasters;
    private JTextField txtNombrePodcaster, txtGeneroPodcaster, txtDescripcionPodcaster;

    // Componentes para Podcasts
    private DefaultListModel<Podcast> modeloPodcasts;
    private JList<Podcast> listaPodcasts;
    private JTextField txtNombrePodcast, txtArchivoPodcast, txtDuracionPodcast, txtParticipantesPodcast;
    private JTextArea txtResumen;

    public PanelAdminPodcasts(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.gestor = ventana.getGestorCliente();

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(12, 12));

        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Podcasters", crearPanelPodcasters());
        tabbedPane.addTab("Podcasts", crearPanelPodcasts());

        // Panel de botones generales
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnVolver = new JButton("Volver");
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVolver);

        txtResumen = new JTextArea(3, 20);
        txtResumen.setEditable(false);

        add(tabbedPane, BorderLayout.CENTER);
        add(new JScrollPane(txtResumen), BorderLayout.SOUTH);
        add(panelBotones, BorderLayout.NORTH);

        btnActualizar.addActionListener(e -> refrescar());
        btnVolver.addActionListener(e -> ventana.cambiarPanel("admin"));
    }

    private JPanel crearPanelPodcasters() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloPodcasters = new DefaultListModel<>();
        listaPodcasters = new JList<>(modeloPodcasters);
        listaPodcasters.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Podcaster p = listaPodcasters.getSelectedValue();
                if (p != null) {
                    txtNombrePodcaster.setText(p.getNombreArt());
                    txtGeneroPodcaster.setText(p.getGenero());
                    txtDescripcionPodcaster.setText(p.getDescripcion());
                }
            }
        });

        // Formulario de podcasters
        JPanel formPodcasters = new JPanel(new GridLayout(5, 2, 10, 10));
        formPodcasters.add(new JLabel("Nombre:"));
        txtNombrePodcaster = new JTextField();
        formPodcasters.add(txtNombrePodcaster);

        formPodcasters.add(new JLabel("Género:"));
        txtGeneroPodcaster = new JTextField();
        formPodcasters.add(txtGeneroPodcaster);

        formPodcasters.add(new JLabel("Descripción:"));
        txtDescripcionPodcaster = new JTextField();
        formPodcasters.add(txtDescripcionPodcaster);

        JButton btnAgregarPodcaster = new JButton("Agregar Podcaster");
        JButton btnActualizarPodcaster = new JButton("Actualizar");
        JButton btnEliminarPodcaster = new JButton("Eliminar");

        formPodcasters.add(btnAgregarPodcaster);
        formPodcasters.add(btnActualizarPodcaster);
        formPodcasters.add(btnEliminarPodcaster);

        btnAgregarPodcaster.addActionListener(e -> agregarPodcaster());
        btnActualizarPodcaster.addActionListener(e -> actualizarPodcaster());
        btnEliminarPodcaster.addActionListener(e -> eliminarPodcaster());

        panel.add(new JScrollPane(listaPodcasters), BorderLayout.CENTER);
        panel.add(formPodcasters, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelPodcasts() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloPodcasts = new DefaultListModel<>();
        listaPodcasts = new JList<>(modeloPodcasts);
        listaPodcasts.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Podcast p = listaPodcasts.getSelectedValue();
                if (p != null) {
                    txtNombrePodcast.setText(p.getNombreAudio());
                    txtArchivoPodcast.setText(p.getArchivo());
                    txtDuracionPodcast.setText(String.valueOf(p.getDuracionSegundos()));
                    txtParticipantesPodcast.setText(String.valueOf(p.getNumeroParticipantes()));
                }
            }
        });

        // Formulario de podcasts
        JPanel formPodcasts = new JPanel(new GridLayout(6, 2, 10, 10));
        
        formPodcasts.add(new JLabel("Nombre Podcast:"));
        txtNombrePodcast = new JTextField();
        formPodcasts.add(txtNombrePodcast);

        formPodcasts.add(new JLabel("Archivo:"));
        txtArchivoPodcast = new JTextField();
        formPodcasts.add(txtArchivoPodcast);

        formPodcasts.add(new JLabel("Duración (seg):"));
        txtDuracionPodcast = new JTextField();
        formPodcasts.add(txtDuracionPodcast);

        formPodcasts.add(new JLabel("Participantes:"));
        txtParticipantesPodcast = new JTextField();
        formPodcasts.add(txtParticipantesPodcast);

        JButton btnAgregarPodcast = new JButton("Agregar Podcast");
        JButton btnActualizarPodcast = new JButton("Actualizar");
        JButton btnEliminarPodcast = new JButton("Eliminar");

        formPodcasts.add(btnAgregarPodcast);
        formPodcasts.add(btnActualizarPodcast);
        formPodcasts.add(btnEliminarPodcast);

        btnAgregarPodcast.addActionListener(e -> agregarPodcast());
        btnActualizarPodcast.addActionListener(e -> actualizarPodcast());
        btnEliminarPodcast.addActionListener(e -> eliminarPodcast());

        panel.add(new JScrollPane(listaPodcasts), BorderLayout.CENTER);
        panel.add(formPodcasts, BorderLayout.EAST);

        return panel;
    }

    private void agregarPodcaster() {
        String nombre = txtNombrePodcaster.getText().trim();
        String genero = txtGeneroPodcaster.getText().trim();
        String descripcion = txtDescripcionPodcaster.getText().trim();

        if (nombre.isEmpty()) {
            txtResumen.setText("Error: Ingrese nombre del podcaster");
            return;
        }

        Podcaster podcaster = new Podcaster(0, nombre, genero, descripcion, "");
        if (gestor.crearPodcaster(podcaster)) {
            txtResumen.setText("Podcaster agregado exitosamente");
            limpiarFormularioPodcasters();
            refrescar();
        } else {
            txtResumen.setText("Error al agregar podcaster");
        }
    }

    private void actualizarPodcaster() {
        Podcaster p = listaPodcasters.getSelectedValue();
        if (p == null) {
            txtResumen.setText("Error: Seleccione un podcaster");
            return;
        }

        String nombre = txtNombrePodcaster.getText().trim();
        String descripcion = txtDescripcionPodcaster.getText().trim();

        if (gestor.actualizarPodcaster(p.getId(), nombre, descripcion, p.getFoto())) {
            txtResumen.setText("Podcaster actualizado exitosamente");
            refrescar();
        } else {
            txtResumen.setText("Error al actualizar podcaster");
        }
    }

    private void eliminarPodcaster() {
        Podcaster p = listaPodcasters.getSelectedValue();
        if (p == null) {
            txtResumen.setText("Error: Seleccione un podcaster");
            return;
        }

        if (gestor.eliminarPodcaster(p.getId())) {
            txtResumen.setText("Podcaster eliminado exitosamente");
            limpiarFormularioPodcasters();
            refrescar();
        } else {
            txtResumen.setText("Error al eliminar podcaster");
        }
    }

    private void agregarPodcast() {
        String nombre = txtNombrePodcast.getText().trim();
        String archivo = txtArchivoPodcast.getText().trim();
        String duracionStr = txtDuracionPodcast.getText().trim();
        String participantesStr = txtParticipantesPodcast.getText().trim();

        if (nombre.isEmpty() || duracionStr.isEmpty() || participantesStr.isEmpty()) {
            txtResumen.setText("Error: Ingrese todos los datos del podcast");
            return;
        }

        try {
            int duracion = Integer.parseInt(duracionStr);
            int participantes = Integer.parseInt(participantesStr);

            // Usar el primer podcaster si existe
            ArrayList<Podcaster> podcasters = gestor.obtenerPodcasters();
            if (podcasters.isEmpty()) {
                txtResumen.setText("Error: Primero debe crear un podcaster");
                return;
            }

            int idPodcaster = podcasters.get(0).getId();
            Podcast podcast = new Podcast(0, nombre, archivo, duracion, 0, idPodcaster, participantes, "podcast");
            
            if (gestor.crearPodcast(podcast)) {
                txtResumen.setText("Podcast agregado exitosamente");
                limpiarFormularioPodcasts();
                refrescar();
            } else {
                txtResumen.setText("Error al agregar podcast");
            }
        } catch (NumberFormatException ex) {
            txtResumen.setText("Error: Duración y participantes deben ser números");
        }
    }

    private void actualizarPodcast() {
        Podcast p = listaPodcasts.getSelectedValue();
        if (p == null) {
            txtResumen.setText("Error: Seleccione un podcast");
            return;
        }

        String nombre = txtNombrePodcast.getText().trim();
        String archivo = txtArchivoPodcast.getText().trim();
        String duracionStr = txtDuracionPodcast.getText().trim();
        String participantesStr = txtParticipantesPodcast.getText().trim();

        try {
            int duracion = Integer.parseInt(duracionStr);
            int participantes = Integer.parseInt(participantesStr);

            if (gestor.actualizarPodcast(p.getId(), nombre, archivo, duracion, participantes, "", p.getIdPodcaster())) {
                txtResumen.setText("Podcast actualizado exitosamente");
                refrescar();
            } else {
                txtResumen.setText("Error al actualizar podcast");
            }
        } catch (NumberFormatException ex) {
            txtResumen.setText("Error: Duración y participantes deben ser números");
        }
    }

    private void eliminarPodcast() {
        Podcast p = listaPodcasts.getSelectedValue();
        if (p == null) {
            txtResumen.setText("Error: Seleccione un podcast");
            return;
        }

        if (gestor.eliminarPodcast(p.getId())) {
            txtResumen.setText("Podcast eliminado exitosamente");
            limpiarFormularioPodcasts();
            refrescar();
        } else {
            txtResumen.setText("Error al eliminar podcast");
        }
    }

    private void limpiarFormularioPodcasters() {
        txtNombrePodcaster.setText("");
        txtGeneroPodcaster.setText("");
        txtDescripcionPodcaster.setText("");
        listaPodcasters.clearSelection();
    }

    private void limpiarFormularioPodcasts() {
        txtNombrePodcast.setText("");
        txtArchivoPodcast.setText("");
        txtDuracionPodcast.setText("");
        txtParticipantesPodcast.setText("");
        listaPodcasts.clearSelection();
    }

    @Override
    public void refrescar() {
        // Refrescar lista de podcasters
        modeloPodcasters.clear();
        for (Podcaster podcaster : gestor.obtenerPodcasters()) {
            modeloPodcasters.addElement(podcaster);
        }

        // Refrescar lista de podcasts
        modeloPodcasts.clear();
        for (Podcaster podcaster : gestor.obtenerPodcasters()) {
            for (Podcast podcast : gestor.obtenerPodcasts(podcaster.getNombreArt())) {
                modeloPodcasts.addElement(podcast);
            }
        }
    }
}
