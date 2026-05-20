package controlador;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class SimpleFileController {
    private final Path dir;

    public SimpleFileController(String dirPath) {
        this.dir = Paths.get(dirPath);
        try {
            if (!Files.exists(this.dir)) Files.createDirectories(this.dir);
        } catch (IOException e) {
            System.out.println("Error creando directorio: " + e.getMessage());
        }
    }

    public List<String> readLines(String filename) {
        Path file = dir.resolve(filename);
        try {
            if (!Files.exists(file)) return Collections.emptyList();
            return Files.readAllLines(file);
        } catch (IOException e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean writeLines(String filename, List<String> lines) {
        Path file = dir.resolve(filename);
        try {
            Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Error escribiendo archivo: " + e.getMessage());
            return false;
        }
    }

    public boolean appendLine(String filename, String line) {
        Path file = dir.resolve(filename);
        try {
            Files.write(file, Collections.singletonList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.out.println("Error añadiendo línea: " + e.getMessage());
            return false;
        }
    }
}
