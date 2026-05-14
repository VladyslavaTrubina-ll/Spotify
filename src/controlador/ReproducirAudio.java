package controlador;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import modelo.*;

/**
 * Gestor de reproducción de audio con soporte para canciones y podcasts.
 * Implementa lógica de restricciones para usuarios Free y Premium.
 */
public class ReproductorAudio {
	
	// ============ ATRIBUTOS DE ESTADO Y CONTROL ============
	private int idClienteActual;
	private boolean esClientePremium;
	private ArrayList<Audio> colaReproduccion;
	private int indiceActual;
	private long ultimoSaltoTimestamp;
	private boolean enReproduccion;
	private double velocidadReproduccion; // Para podcasts: 0.5x, 1x, 1.5x, 2x
	private int segundoActual; // Progreso actual en segundos
	private Timer timerProgreso;
	private ControladorDB controladordb;
	private GestorCliente gestorCliente;
	
	// Constantes
	private static final long TIEMPO_MINIMO_SALTO_FREE = 10 * 60 * 1000; // 10 minutos en ms
	private static final String RUTA_EXPORTACION = "exports/";

	// ============ CONSTRUCTORES ============

	public ReproductorAudio(int idCliente, boolean esClientePremium, ControladorDB controladordb, GestorCliente gestorCliente) {
		this.idClienteActual = idCliente;
		this.esClientePremium = esClientePremium;
		this.colaReproduccion = new ArrayList<>();
		this.indiceActual = 0;
		this.ultimoSaltoTimestamp = 0;
		this.enReproduccion = false;
		this.velocidadReproduccion = 1.0;
		this.segundoActual = 0;
		this.timerProgreso = null;
		this.controladordb = controladordb;
		this.gestorCliente = gestorCliente;
		
		// Crear directorio de exportación si no existe
		crearDirectorioExportacion();
	}

	// ============ GESTIÓN DE COLA DE REPRODUCCIÓN ============

	/**
	 * Establece la cola de reproducción
	 */
	public void establecerColaReproduccion(ArrayList<Audio> audios) {
		if (audios == null || audios.isEmpty()) {
			System.out.println("Error: La cola está vacía");
			return;
		}
		
		// Si el usuario es Free, aplicar modo aleatorio
		if (!esClientePremium) {
			this.colaReproduccion = desordenarAleatoriamente(new ArrayList<>(audios));
		} else {
			this.colaReproduccion = new ArrayList<>(audios);
		}
		
		this.indiceActual = 0;
		this.segundoActual = 0;
	}

	/**
	 * Desordena una lista de audios aleatoriamente (Modo Free)
	 */
	private ArrayList<Audio> desordenarAleatoriamente(ArrayList<Audio> audios) {
		Collections.shuffle(audios);
		return audios;
	}

	/**
	 * Obtiene la lista actual de reproducción
	 */
	public ArrayList<Audio> obtenerColaReproduccion() {
		return new ArrayList<>(this.colaReproduccion);
	}

	/**
	 * Obtiene el audio actualmente en reproducción
	 */
	public Audio obtenerAudioActual() {
		if (colaReproduccion.isEmpty() || indiceActual < 0 || indiceActual >= colaReproduccion.size()) {
			return null;
		}
		return colaReproduccion.get(indiceActual);
	}

	// ============ CONTROL DE REPRODUCCIÓN ============

	/**
	 * Inicia la reproducción del audio actual
	 */
	public boolean play() {
		if (colaReproduccion.isEmpty()) {
			System.out.println("Error: Cola de reproducción vacía");
			return false;
		}

		if (enReproduccion) {
			System.out.println("Ya se está reproduciendo");
			return false;
		}

		Audio audioActual = obtenerAudioActual();
		if (audioActual == null) {
			return false;
		}

		// Registrar reproducción en BD
		if (controladordb.startConnection()) {
			incrementarReproduccionesDB(audioActual.getId());
			controladordb.cerrarConexion();
		}

		enReproduccion = true;
		iniciarSimuladorProgreso();
		System.out.println("▶ Reproduciendo: " + audioActual.getNombreAudio());
		return true;
	}

	/**
	 * Pausa la reproducción
	 */
	public boolean pause() {
		if (!enReproduccion) {
			System.out.println("No hay reproducción en curso");
			return false;
		}

		enReproduccion = false;
		if (timerProgreso != null) {
			timerProgreso.cancel();
			timerProgreso = null;
		}
		System.out.println("⏸ Pausa");
		return true;
	}

	/**
	 * Reanuda la reproducción
	 */
	public boolean reanudar() {
		if (colaReproduccion.isEmpty()) {
			return false;
		}

		enReproduccion = true;
		iniciarSimuladorProgreso();
		System.out.println("▶ Reanudando: " + obtenerAudioActual().getNombreAudio());
		return true;
	}

	/**
	 * Detiene la reproducción completamente
	 */
	public void detener() {
		enReproduccion = false;
		if (timerProgreso != null) {
			timerProgreso.cancel();
			timerProgreso = null;
		}
		segundoActual = 0;
		System.out.println("⏹ Reproducción detenida");
	}

	// ============ NAVEGACIÓN CON VALIDACIÓN ============

	/**
	 * Salta al siguiente audio con validación de restricción (10 min para Free)
	 */
	public Audio siguiente() {
		if (!validarSalto()) {
			long tiempoEspera = (TIEMPO_MINIMO_SALTO_FREE - (System.currentTimeMillis() - ultimoSaltoTimestamp)) / 1000;
			System.out.println("⏱ Usuario Free: Debes esperar " + tiempoEspera + " segundos más");
			return null;
		}

		indiceActual++;
		if (indiceActual >= colaReproduccion.size()) {
			indiceActual = 0; // Volver al principio
		}

		segundoActual = 0;
		ultimoSaltoTimestamp = System.currentTimeMillis();
		
		Audio audioActual = obtenerAudioActual();
		System.out.println("⏭ Siguiente: " + audioActual.getNombreAudio());
		return audioActual;
	}

	/**
	 * Vuelve al audio anterior con validación de restricción
	 */
	public Audio anterior() {
		if (!validarSalto()) {
			long tiempoEspera = (TIEMPO_MINIMO_SALTO_FREE - (System.currentTimeMillis() - ultimoSaltoTimestamp)) / 1000;
			System.out.println("⏱ Usuario Free: Debes esperar " + tiempoEspera + " segundos más");
			return null;
		}

		indiceActual--;
		if (indiceActual < 0) {
			indiceActual = colaReproduccion.size() - 1;
		}

		segundoActual = 0;
		ultimoSaltoTimestamp = System.currentTimeMillis();
		
		Audio audioActual = obtenerAudioActual();
		System.out.println("⏮ Anterior: " + audioActual.getNombreAudio());
		return audioActual;
	}

	/**
	 * Salta a una posición específica en la cola
	 */
	public Audio saltarA(int posicion) {
		if (posicion < 0 || posicion >= colaReproduccion.size()) {
			System.out.println("Error: Posición inválida");
			return null;
		}

		if (!validarSalto()) {
			long tiempoEspera = (TIEMPO_MINIMO_SALTO_FREE - (System.currentTimeMillis() - ultimoSaltoTimestamp)) / 1000;
			System.out.println("⏱ Usuario Free: Debes esperar " + tiempoEspera + " segundos más");
			return null;
		}

		indiceActual = posicion;
		segundoActual = 0;
		ultimoSaltoTimestamp = System.currentTimeMillis();
		
		Audio audioActual = obtenerAudioActual();
		System.out.println("⏭ Saltando a: " + audioActual.getNombreAudio());
		return audioActual;
	}

	/**
	 * Valida si un usuario Free puede hacer un salto (10 minutos entre saltos)
	 */
	private boolean validarSalto() {
		if (esClientePremium) {
			return true; // Premium sin restricción
		}

		long tiempoTranscurrido = System.currentTimeMillis() - ultimoSaltoTimestamp;
		return tiempoTranscurrido >= TIEMPO_MINIMO_SALTO_FREE;
	}

	// ============ CONTROL DE VELOCIDAD ============

	/**
	 * Establece la velocidad de reproducción (para Podcasts)
	 * Valores válidos: 0.5, 1.0, 1.5, 2.0
	 */
	public boolean establecerVelocidad(double velocidad) {
		if (velocidad < 0.5 || velocidad > 2.0) {
			System.out.println("Error: Velocidad debe estar entre 0.5x y 2.0x");
			return false;
		}

		this.velocidadReproduccion = velocidad;
		System.out.println("Velocidad: " + velocidad + "x");
		return true;
	}

	/**
	 * Obtiene la velocidad actual
	 */
	public double obtenerVelocidad() {
		return velocidadReproduccion;
	}

	// ============ FORMATEO Y PROGRESO ============

	/**
	 * Formatea segundos a formato mm:ss (ej: 2:14)
	 */
	public static String formatearTiempo(int segundos) {
		int minutos = segundos / 60;
		int secs = segundos % 60;
		return String.format("%d:%02d", minutos, secs);
	}

	/**
	 * Obtiene el progreso actual en formato "mm:ss / mm:ss"
	 */
	public String obtenerProgreso() {
		Audio actual = obtenerAudioActual();
		if (actual == null) {
			return "00:00 / 00:00";
		}

		return formatearTiempo(segundoActual) + " / " + formatearTiempo(actual.getDuratasecondi());
	}

	/**
	 * Obtiene el porcentaje de progreso (0-100)
	 */
	public int obtenerPorcentajeProgreso() {
		Audio actual = obtenerAudioActual();
		if (actual == null || actual.getDuratasecondi() == 0) {
			return 0;
		}

		return (int) ((segundoActual * 100.0) / actual.getDuratasecondi());
	}

	/**
	 * Simulador de progreso que incrementa el contador cada segundo
	 */
	private void iniciarSimuladorProgreso() {
		if (timerProgreso != null) {
			timerProgreso.cancel();
		}

		timerProgreso = new Timer();
		timerProgreso.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (enReproduccion && !colaReproduccion.isEmpty()) {
					Audio actual = obtenerAudioActual();
					if (actual != null) {
						segundoActual++;

						// Si llegó al final, pasar al siguiente
						if (segundoActual >= actual.getDuratasecondi()) {
							siguiente();
						}
					}
				}
			}
		}, 1000, 1000); // Ejecutar cada 1 segundo
	}

	// ============ GESTIÓN DE FAVORITOS ============

	/**
	 * Añade el audio actual a favoritos
	 */
	public boolean agregarAFavoritos() {
		Audio audioActual = obtenerAudioActual();
		if (audioActual == null) {
			System.out.println("Error: No hay audio en reproducción");
			return false;
		}

		if (controladordb.startConnection()) {
			try {
				String sql = "INSERT INTO favoritos (idCliente, idAudio) VALUES (?, ?)";
				java.sql.PreparedStatement ps = controladordb.getConnection().prepareStatement(sql);
				ps.setInt(1, idClienteActual);
				ps.setInt(2, audioActual.getId());
				
				boolean resultado = ps.executeUpdate() > 0;
				if (resultado) {
					System.out.println("♥ Añadido a favoritos: " + audioActual.getNombreAudio());
				}
				ps.close();
				controladordb.cerrarConexion();
				return resultado;
			} catch (Exception e) {
				System.out.println("Error añadiendo a favoritos: " + e.getMessage());
				controladordb.cerrarConexion();
			}
		}
		return false;
	}

	/**
	 * Elimina el audio actual de favoritos
	 */
	public boolean eliminarDeFavoritos() {
		Audio audioActual = obtenerAudioActual();
		if (audioActual == null) {
			return false;
		}

		if (controladordb.startConnection()) {
			try {
				String sql = "DELETE FROM favoritos WHERE idCliente = ? AND idAudio = ?";
				java.sql.PreparedStatement ps = controladordb.getConnection().prepareStatement(sql);
				ps.setInt(1, idClienteActual);
				ps.setInt(2, audioActual.getId());
				
				boolean resultado = ps.executeUpdate() > 0;
				if (resultado) {
					System.out.println("♡ Eliminado de favoritos: " + audioActual.getNombreAudio());
				}
				ps.close();
				controladordb.cerrarConexion();
				return resultado;
			} catch (Exception e) {
				System.out.println("Error eliminando de favoritos: " + e.getMessage());
				controladordb.cerrarConexion();
			}
		}
		return false;
	}

	// ============ INTEGRACIÓN CON BD ============

	/**
	 * Incrementa el contador de reproducciones en BD
	 */
	private void incrementarReproduccionesDB(int idAudio) {
		try {
			String sql = "UPDATE audio SET nReproducciones = nReproducciones + 1 WHERE idAudio = ?";
			java.sql.PreparedStatement ps = controladordb.getConnection().prepareStatement(sql);
			ps.setInt(1, idAudio);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			System.out.println("Error actualizando reproducciones: " + e.getMessage());
		}
	}

	// ============ EXPORTACIÓN DE METADATOS ============

	/**
	 * Crea el directorio de exportación si no existe
	 */
	private void crearDirectorioExportacion() {
		File dir = new File(RUTA_EXPORTACION);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * Exporta los metadatos del audio actual a un archivo
	 */
	public boolean exportarMetadatos() {
		Audio audioActual = obtenerAudioActual();
		if (audioActual == null) {
			System.out.println("Error: No hay audio para exportar");
			return false;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String timestamp = sdf.format(new java.util.Date());
			String nombreArchivo = RUTA_EXPORTACION + "metadata_" + audioActual.getId() + "_" + timestamp + ".txt";

			FileWriter writer = new FileWriter(nombreArchivo);
			
			writer.write("===== METADATOS DE AUDIO =====\n");
			writer.write("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n");
			writer.write("ID: " + audioActual.getId() + "\n");
			writer.write("Nombre: " + audioActual.getNombreAudio() + "\n");
			writer.write("Tipo: " + audioActual.getTipo() + "\n");
			writer.write("Duración: " + formatearTiempo(audioActual.getDuratasecondi()) + "\n");
			writer.write("Reproducciones: " + audioActual.getNumRep() + "\n");
			writer.write("Archivo: " + audioActual.getArchivo() + "\n");
			writer.write("Usuario: " + idClienteActual + "\n");
			writer.write("Tipo Usuario: " + (esClientePremium ? "Premium" : "Free") + "\n");
			
			if (audioActual instanceof Cancion) {
				Cancion cancion = (Cancion) audioActual;
				writer.write("Colaboradores: " + (cancion.getNombresColaboradores() != null ? cancion.getNombresColaboradores() : "N/A") + "\n");
			} else if (audioActual instanceof Podcast) {
				Podcast podcast = (Podcast) audioActual;
				writer.write("Velocidad reproducción: " + velocidadReproduccion + "x\n");
			}
			
			writer.write("Progreso: " + obtenerProgreso() + "\n");
			writer.write("Porcentaje: " + obtenerPorcentajeProgreso() + "%\n");
			writer.close();

			System.out.println("✓ Metadatos exportados a: " + nombreArchivo);
			return true;
		} catch (IOException e) {
			System.out.println("Error exportando metadatos: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Exporta toda la cola de reproducción a un archivo
	 */
	public boolean exportarCola() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String timestamp = sdf.format(new java.util.Date());
			String nombreArchivo = RUTA_EXPORTACION + "cola_" + timestamp + ".txt";

			FileWriter writer = new FileWriter(nombreArchivo);
			
			writer.write("===== COLA DE REPRODUCCIÓN =====\n");
			writer.write("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n");
			writer.write("Usuario: " + idClienteActual + "\n");
			writer.write("Total de audios: " + colaReproduccion.size() + "\n");
			writer.write("Audio actual: " + (indiceActual + 1) + "\n");
			writer.write("=========================================\n\n");

			for (int i = 0; i < colaReproduccion.size(); i++) {
				Audio audio = colaReproduccion.get(i);
				String prefijo = (i == indiceActual) ? "▶ " : "  ";
				writer.write(prefijo + (i + 1) + ". " + audio.getNombreAudio() + 
						" [" + formatearTiempo(audio.getDuratasecondi()) + "]\n");
			}

			writer.close();

			System.out.println("✓ Cola exportada a: " + nombreArchivo);
			return true;
		} catch (IOException e) {
			System.out.println("Error exportando cola: " + e.getMessage());
			return false;
		}
	}

	// ============ INFORMACIÓN Y ESTADO ============

	/**
	 * Obtiene información detallada del audio actual
	 */
	public String obtenerInformacionActual() {
		Audio audio = obtenerAudioActual();
		if (audio == null) {
			return "No hay audio en reproducción";
		}

		StringBuilder info = new StringBuilder();
		info.append("=== AUDIO ACTUAL ===\n");
		info.append("Título: ").append(audio.getNombreAudio()).append("\n");
		info.append("Tipo: ").append(audio.getTipo()).append("\n");
		info.append("Duración: ").append(formatearTiempo(audio.getDuratasecondi())).append("\n");
		info.append("Progreso: ").append(obtenerProgreso()).append(" (").append(obtenerPorcentajeProgreso()).append("%)\n");
		info.append("Reproducciones: ").append(audio.getNumRep()).append("\n");
		info.append("Estado: ").append(enReproduccion ? "▶ En reproducción" : "⏸ Pausado").append("\n");
		info.append("Posición en cola: ").append((indiceActual + 1)).append(" de ").append(colaReproduccion.size()).append("\n");
		
		if (!esClientePremium) {
			info.append("Restricción Free: ").append(validarSalto() ? "✓ Puedes saltar" : "✗ Espera antes de saltar").append("\n");
		}

		return info.toString();
	}

	/**
	 * Obtiene resumen de la cola
	 */
	public String obtenerResumenCola() {
		if (colaReproduccion.isEmpty()) {
			return "Cola vacía";
		}

		StringBuilder resumen = new StringBuilder();
		resumen.append("=== COLA DE REPRODUCCIÓN (").append(colaReproduccion.size()).append(" audios) ===\n");

		int inicio = Math.max(0, indiceActual - 2);
		int fin = Math.min(colaReproduccion.size(), indiceActual + 3);

		if (inicio > 0) {
			resumen.append("...\n");
		}

		for (int i = inicio; i < fin; i++) {
			Audio audio = colaReproduccion.get(i);
			if (i == indiceActual) {
				resumen.append("▶ ").append(i + 1).append(". ").append(audio.getNombreAudio())
						.append(" [").append(formatearTiempo(audio.getDuratasecondi())).append("]\n");
			} else {
				resumen.append("  ").append(i + 1).append(". ").append(audio.getNombreAudio())
						.append(" [").append(formatearTiempo(audio.getDuratasecondi())).append("]\n");
			}
		}

		if (fin < colaReproduccion.size()) {
			resumen.append("...\n");
		}

		return resumen.toString();
	}

}
