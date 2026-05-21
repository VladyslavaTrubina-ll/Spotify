package panel;

/**
 * Interfaz simple para paneles que deben exponer un método `refrescar()`
 * para recargar su contenido desde el gestor/DAO cuando cambian los datos.
 */
public interface PanelRefrescable {
	void refrescar();
}