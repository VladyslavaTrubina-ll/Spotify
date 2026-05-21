package panel;

import controlador.GestorClienteNuevo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import modelo.*;
import vista.VentanaPrincipal;

/**
 * Panel de administración para gestionar artistas, álbumes y canciones
 */
public class PanelAdminMusica extends JPanel implements PanelRefrescable {

    private final VentanaPrincipal ventana;
    private final GestorClienteNuevo gestor;

    // Componentes para Artistas
    private DefaultListModel<Musico> modeloArtistas;
    private JList<Musico> listaArtistas;
    private JTextField txtNombreArtista, txtGeneroArtista, txtDescripcionArtista;
    private JComboBox<String> cmbCaracteristicaArtista;
    private JTextArea txtResumen;

    // Componentes para Álbumes
    private DefaultListModel<Album> modeloAlbumes;
    private JList<Album> listaAlbumes;
    private JTextField txtNombreAlbum, txtAnoAlbum, txtGeneroAlbum;
    private JComboBox<String> cmbArtistasAlbum;

    // Componentes para Canciones
    private DefaultListModel<Cancion> modeloCanciones;
    private JList<Cancion> listaCanciones;
    private JTextField txtNombreCancion, txtArchivoCancion, txtDuracionCancion, txtColaboradores;
    private JComboBox<String> cmbAlbumCancion;

    public PanelAdminMusica(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.gestor = ventana.getGestorCliente();

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(12, 12));

        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Artistas", crearPanelArtistas());
        tabbedPane.addTab("Álbumes", crearPanelAlbumes());
        tabbedPane.addTab("Canciones", crearPanelCanciones());

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

    private JPanel crearPanelArtistas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloArtistas = new DefaultListModel<>();
        listaArtistas = new JList<>(modeloArtistas);
        listaArtistas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Musico m = listaArtistas.getSelectedValue();
                if (m != null) {
                    txtNombreArtista.setText(m.getNombreArt());
                    txtGeneroArtista.setText(m.getGenero());
                    txtDescripcionArtista.setText(m.getDescripcion());
                    cmbCaracteristicaArtista.setSelectedItem(m.getComposicion() != null && m.getComposicion().equalsIgnoreCase("Grupo") ? "Grupo" : "Solista");
                }
            }
        });

        // Formulario de artistas
        JPanel formArtistas = new JPanel(new GridLayout(6, 2, 10, 10));
        formArtistas.add(new JLabel("Nombre:"));
        txtNombreArtista = new JTextField();
        formArtistas.add(txtNombreArtista);

        formArtistas.add(new JLabel("Género:"));
        txtGeneroArtista = new JTextField();
        formArtistas.add(txtGeneroArtista);

        formArtistas.add(new JLabel("Descripción:"));
        txtDescripcionArtista = new JTextField();
        formArtistas.add(txtDescripcionArtista);

        formArtistas.add(new JLabel("Característica:"));
        cmbCaracteristicaArtista = new JComboBox<>(new String[] {"Solista", "Grupo"});
        formArtistas.add(cmbCaracteristicaArtista);

        JButton btnAgregarArtista = new JButton("Agregar Artista");
        JButton btnActualizarArtista = new JButton("Actualizar");
        JButton btnEliminarArtista = new JButton("Eliminar");

        formArtistas.add(btnAgregarArtista);
        formArtistas.add(btnActualizarArtista);
        formArtistas.add(btnEliminarArtista);

        btnAgregarArtista.addActionListener(e -> agregarArtista());
        btnActualizarArtista.addActionListener(e -> actualizarArtista());
        btnEliminarArtista.addActionListener(e -> eliminarArtista());

        panel.add(new JScrollPane(listaArtistas), BorderLayout.CENTER);
        panel.add(formArtistas, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelAlbumes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloAlbumes = new DefaultListModel<>();
        listaAlbumes = new JList<>(modeloAlbumes);
        listaAlbumes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Album a = listaAlbumes.getSelectedValue();
                if (a != null) {
                    txtNombreAlbum.setText(a.getTitulo());
                    txtAnoAlbum.setText(a.getFechaPub());
                    txtGeneroAlbum.setText(a.getGenero());
                }
            }
        });

        // Formulario de álbumes
        JPanel formAlbumes = new JPanel(new GridLayout(6, 2, 10, 10));
        formAlbumes.add(new JLabel("Artista:"));
        cmbArtistasAlbum = new JComboBox<>();
        formAlbumes.add(cmbArtistasAlbum);

        formAlbumes.add(new JLabel("Nombre Álbum:"));
        txtNombreAlbum = new JTextField();
        formAlbumes.add(txtNombreAlbum);

        formAlbumes.add(new JLabel("Año:"));
        txtAnoAlbum = new JTextField();
        formAlbumes.add(txtAnoAlbum);

        formAlbumes.add(new JLabel("Género:"));
        txtGeneroAlbum = new JTextField();
        formAlbumes.add(txtGeneroAlbum);

        JButton btnAgregarAlbum = new JButton("Agregar Álbum");
        JButton btnActualizarAlbum = new JButton("Actualizar");
        JButton btnEliminarAlbum = new JButton("Eliminar");

        formAlbumes.add(btnAgregarAlbum);
        formAlbumes.add(btnActualizarAlbum);
        formAlbumes.add(btnEliminarAlbum);

        btnAgregarAlbum.addActionListener(e -> agregarAlbum());
        btnActualizarAlbum.addActionListener(e -> actualizarAlbum());
        btnEliminarAlbum.addActionListener(e -> eliminarAlbum());

        panel.add(new JScrollPane(listaAlbumes), BorderLayout.CENTER);
        panel.add(formAlbumes, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelCanciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloCanciones = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloCanciones);
        listaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cancion c = listaCanciones.getSelectedValue();
                if (c != null) {
                    txtNombreCancion.setText(c.getNombreAudio());
                    txtArchivoCancion.setText(c.getArchivo());
                    txtDuracionCancion.setText(String.valueOf(c.getDuracionSegundos()));
                    txtColaboradores.setText(c.getNombresColaboradores());
                }
            }
        });

        // Formulario de canciones
        JPanel formCanciones = new JPanel(new GridLayout(7, 2, 10, 10));
        formCanciones.add(new JLabel("Álbum:"));
        cmbAlbumCancion = new JComboBox<>();
        formCanciones.add(cmbAlbumCancion);

        formCanciones.add(new JLabel("Nombre Canción:"));
        txtNombreCancion = new JTextField();
        formCanciones.add(txtNombreCancion);

        formCanciones.add(new JLabel("Archivo:"));
        txtArchivoCancion = new JTextField();
        formCanciones.add(txtArchivoCancion);

        formCanciones.add(new JLabel("Duración (seg):"));
        txtDuracionCancion = new JTextField();
        formCanciones.add(txtDuracionCancion);

        formCanciones.add(new JLabel("Colaboradores:"));
        txtColaboradores = new JTextField();
        formCanciones.add(txtColaboradores);

        JButton btnAgregarCancion = new JButton("Agregar Canción");
        JButton btnActualizarCancion = new JButton("Actualizar");
        JButton btnEliminarCancion = new JButton("Eliminar");

        formCanciones.add(btnAgregarCancion);
        formCanciones.add(btnActualizarCancion);
        formCanciones.add(btnEliminarCancion);

        btnAgregarCancion.addActionListener(e -> agregarCancion());
        btnActualizarCancion.addActionListener(e -> actualizarCancion());
        btnEliminarCancion.addActionListener(e -> eliminarCancion());

        panel.add(new JScrollPane(listaCanciones), BorderLayout.CENTER);
        panel.add(formCanciones, BorderLayout.EAST);

        return panel;
    }

    private void agregarArtista() {
        String nombre = txtNombreArtista.getText().trim();
        String genero = txtGeneroArtista.getText().trim();
        String descripcion = txtDescripcionArtista.getText().trim();
        String caracteristica = (String) cmbCaracteristicaArtista.getSelectedItem();

        if (nombre.isEmpty()) {
            txtResumen.setText("Error: Ingrese nombre del artista");
            return;
        }

        // Verificar si el artista ya existe
        if (gestor.existeArtista(nombre)) {
            txtResumen.setText("Error: El artista '" + nombre + "' ya existe");
            return;
        }

        Musico musico = new Musico(0, nombre, genero, descripcion, "", caracteristica);
        if (gestor.crearMusico(musico)) {
            txtResumen.setText("Artista agregado exitosamente");
            limpiarFormularioArtistas();
            refrescar();
        } else {
            txtResumen.setText("Error al agregar artista");
        }
    }

    private void actualizarArtista() {
        Musico m = listaArtistas.getSelectedValue();
        if (m == null) {
            txtResumen.setText("Error: Seleccione un artista");
            return;
        }

        String nombre = txtNombreArtista.getText().trim();
        String genero = txtGeneroArtista.getText().trim();
        String descripcion = txtDescripcionArtista.getText().trim();

        if (gestor.actualizarArtista(m.getId(), nombre, genero, descripcion, m.getFoto())) {
            txtResumen.setText("Artista actualizado exitosamente");
            refrescar();
        } else {
            txtResumen.setText("Error al actualizar artista");
        }
    }

    private void eliminarArtista() {
        Musico m = listaArtistas.getSelectedValue();
        if (m == null) {
            txtResumen.setText("Error: Seleccione un artista");
            return;
        }

        if (gestor.eliminarArtista(m.getId())) {
            txtResumen.setText("Artista eliminado exitosamente");
            limpiarFormularioArtistas();
            refrescar();
        } else {
            txtResumen.setText("Error al eliminar artista");
        }
    }

    private void agregarAlbum() {
        String nombre = txtNombreAlbum.getText().trim();
        String ano = txtAnoAlbum.getText().trim();
        String genero = txtGeneroAlbum.getText().trim();
        String artistaSeleccionado = (String) cmbArtistasAlbum.getSelectedItem();

        if (nombre.isEmpty() || ano.isEmpty() || artistaSeleccionado == null) {
            txtResumen.setText("Error: Ingrese todos los datos del álbum");
            return;
        }
        
        if (!ano.matches("\\d{4}")) {
            txtResumen.setText("Error: El año debe tener 4 dígitos");
            return;
        }

        // Verificar si el álbum ya existe
        if (gestor.getControladorDB().existeAlbum(nombre)) {
            txtResumen.setText("Error: El álbum '" + nombre + "' ya existe");
            return;
        }

        // Obtener ID del artista seleccionado
        ArrayList<Musico> musicos = gestor.obtenerMusicos();
        int idMusico = 0;
        for (Musico m : musicos) {
            if (m.getNombreArt().equals(artistaSeleccionado)) {
                idMusico = m.getId();
                break;
            }
        }

        Album album = new Album(0, nombre, ano, genero, "", idMusico);
        if (gestor.crearAlbum(album)) {
            txtResumen.setText("Álbum agregado exitosamente");
            limpiarFormularioAlbumes();
            refrescar();
        } else {
            txtResumen.setText("Error al agregar álbum");
        }
    }

    private void actualizarAlbum() {
        Album a = listaAlbumes.getSelectedValue();
        if (a == null) {
            txtResumen.setText("Error: Seleccione un álbum");
            return;
        }

        String nombre = txtNombreAlbum.getText().trim();
        String ano = txtAnoAlbum.getText().trim();
        String genero = txtGeneroAlbum.getText().trim();
        
        if (ano.isEmpty() || !ano.matches("\\d{4}")) {
            txtResumen.setText("Error: El año debe tener 4 dígitos");
            return;
        }

        if (gestor.actualizarAlbum(a.getId(), nombre, ano, genero, a.getFoto(), a.getIdMusico())) {
            txtResumen.setText("Álbum actualizado exitosamente");
            refrescar();
        } else {
            txtResumen.setText("Error al actualizar álbum");
        }
    }

    private void eliminarAlbum() {
        Album a = listaAlbumes.getSelectedValue();
        if (a == null) {
            txtResumen.setText("Error: Seleccione un álbum");
            return;
        }

        if (gestor.eliminarAlbum(a.getId())) {
            txtResumen.setText("Álbum eliminado exitosamente");
            limpiarFormularioAlbumes();
            refrescar();
        } else {
            txtResumen.setText("Error al eliminar álbum");
        }
    }

    private void agregarCancion() {
        String nombre = txtNombreCancion.getText().trim();
        String archivo = txtArchivoCancion.getText().trim();
        String durationStr = txtDuracionCancion.getText().trim();
        String colaboradores = txtColaboradores.getText().trim();
        String albumSeleccionado = (String) cmbAlbumCancion.getSelectedItem();

        if (nombre.isEmpty() || durationStr.isEmpty() || albumSeleccionado == null) {
            txtResumen.setText("Error: Ingrese todos los datos de la canción");
            return;
        }

        // Verificar si la canción ya existe
        if (gestor.getControladorDB().existeCancion(nombre)) {
            txtResumen.setText("Error: La canción '" + nombre + "' ya existe");
            return;
        }

        try {
            int duracion = Integer.parseInt(durationStr);

            // Obtener ID del álbum seleccionado
            ArrayList<Album> albumes = gestor.obtenerDiscografia("");
            int idAlbum = 0;
            for (Album album : albumes) {
                if (album.getTitulo().equals(albumSeleccionado)) {
                    idAlbum = album.getId();
                    break;
                }
            }

            Cancion cancion = new Cancion(0, nombre, archivo, duracion, 0, idAlbum, colaboradores, "cancion");
            if (gestor.crearCancion(cancion)) {
                txtResumen.setText("Canción agregada exitosamente");
                limpiarFormularioCanciones();
                refrescar();
            } else {
                txtResumen.setText("Error al agregar canción");
            }
        } catch (NumberFormatException ex) {
            txtResumen.setText("Error: La duración debe ser un número");
        }
    }

    private void actualizarCancion() {
        Cancion c = listaCanciones.getSelectedValue();
        if (c == null) {
            txtResumen.setText("Error: Seleccione una canción");
            return;
        }

        String nombre = txtNombreCancion.getText().trim();
        String archivo = txtArchivoCancion.getText().trim();
        String durationStr = txtDuracionCancion.getText().trim();
        String colaboradores = txtColaboradores.getText().trim();

        try {
            if (gestor.actualizarCancion(c.getId(), nombre, archivo, durationStr, c.getIdAlbum(), colaboradores)) {
                txtResumen.setText("Canción actualizada exitosamente");
                refrescar();
            } else {
                txtResumen.setText("Error al actualizar canción");
            }
        } catch (NumberFormatException ex) {
            txtResumen.setText("Error: La duración debe ser un número");
        }
    }

    private void eliminarCancion() {
        Cancion c = listaCanciones.getSelectedValue();
        if (c == null) {
            txtResumen.setText("Error: Seleccione una canción");
            return;
        }

        if (gestor.eliminarCancion(c.getId())) {
            txtResumen.setText("Canción eliminada exitosamente");
            limpiarFormularioCanciones();
            refrescar();
        } else {
            txtResumen.setText("Error al eliminar canción");
        }
    }

    private void limpiarFormularioArtistas() {
        txtNombreArtista.setText("");
        txtGeneroArtista.setText("");
        txtDescripcionArtista.setText("");
        cmbCaracteristicaArtista.setSelectedIndex(0);
        listaArtistas.clearSelection();
    }

    private void limpiarFormularioAlbumes() {
        txtNombreAlbum.setText("");
        txtAnoAlbum.setText("");
        txtGeneroAlbum.setText("");
        listaAlbumes.clearSelection();
    }

    private void limpiarFormularioCanciones() {
        txtNombreCancion.setText("");
        txtArchivoCancion.setText("");
        txtDuracionCancion.setText("");
        txtColaboradores.setText("");
        listaCanciones.clearSelection();
    }

    @Override
    public void refrescar() {
        // Refrescar lista de artistas
        modeloArtistas.clear();
        for (Musico musico : gestor.obtenerMusicos()) {
            modeloArtistas.addElement(musico);
        }

        // Refrescar combo de artistas para álbumes
        String artistaSeleccionado = (String) cmbArtistasAlbum.getSelectedItem();
        cmbArtistasAlbum.removeAllItems();
        for (Musico musico : gestor.obtenerMusicos()) {
            cmbArtistasAlbum.addItem(musico.getNombreArt());
        }
        if (artistaSeleccionado != null) {
            cmbArtistasAlbum.setSelectedItem(artistaSeleccionado);
        }

        // Refrescar lista de álbumes
        modeloAlbumes.clear();
        ArrayList<Album> todosAlbumes = new ArrayList<>();
        for (Musico musico : gestor.obtenerMusicos()) {
            for (Album album : gestor.obtenerDiscografia(musico.getNombreArt())) {
                modeloAlbumes.addElement(album);
                todosAlbumes.add(album);
            }
        }

        // Refrescar combo de álbumes para canciones
        String albumSeleccionado = (String) cmbAlbumCancion.getSelectedItem();
        cmbAlbumCancion.removeAllItems();
        for (Album album : todosAlbumes) {
            cmbAlbumCancion.addItem(album.getTitulo());
        }
        if (albumSeleccionado != null) {
            cmbAlbumCancion.setSelectedItem(albumSeleccionado);
        }

        // Refrescar lista de canciones
        modeloCanciones.clear();
        for (Album album : todosAlbumes) {
            for (Cancion cancion : gestor.obtenerCancionesAlbum(album.getTitulo())) {
                modeloCanciones.addElement(cancion);
            }
        }
    }
}
