import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class S2VSerializer {

    private S2VSerializer() {}

    /** Devuelve el contenido de la hoja en formato S2V (string). */
    public static String toS2V(HojaCalculo hoja) {
        if (hoja == null) throw new IllegalArgumentException("Hoja nula");

        // 1) Encontrar el rectángulo mínimo que cubre todas las celdas existentes
        int maxFila = 0;
        int maxCol = 0;

        for (Map.Entry<Coordenada, Celda> e : hoja.getCeldas().entrySet()) {
            Coordenada c = e.getKey();
            if (c.getFila() > maxFila) maxFila = c.getFila();
            if (c.getColumna() > maxCol) maxCol = c.getColumna();
        }

        // Si no hay ninguna celda, S2V vacío
        if (maxFila == 0 || maxCol == 0) return "";

        // 2) Construir líneas: cada fila una línea, celdas separadas por ';'
        StringBuilder sb = new StringBuilder();

        for (int fila = 1; fila <= maxFila; fila++) {
            for (int col = 1; col <= maxCol; col++) {
                if (col > 1) sb.append(';');

                Celda celda = hoja.getCelda(new Coordenada(fila, col));
                if (celda == null) {
                    // celda inexistente => vacía
                    // (no ponemos nada, el separador ya representa vacío)
                    continue;
                }

                sb.append(serializeCell(celda));
            }
            if (fila < maxFila) sb.append('\n');
        }

        return sb.toString();
    }

    /** Serializa una celda a texto S2V. */
    private static String serializeCell(Celda celda) {
        if (celda == null) return "";

        Contenido cont = celda.getContenido();
        Valor val = celda.getValor();

        // Si es fórmula: guardamos el contenido como "=..." (no el valor)
        if (cont instanceof ContenidoFormula) {
            String expr = ((ContenidoFormula) cont).getExpresion(); // ya es SIN '='
            // Requisito: reemplazar ';' por ',' dentro de la fórmula para evitar conflicto con separador de celdas
            expr = expr.replace(';', ',');
            return "=" + expr;
        }

        // Si no es fórmula: guardamos valor (número o texto)
        if (val == null) return "";

        if (val.esNumero()) {
            // número
            return Double.toString(val.comoNumero());
        }

        // texto
        return val.toString();
    }
    /** Guarda la hoja en un fichero .s2v. */
    public static void saveToFile(HojaCalculo hoja, Path path) {
        if (path == null) throw new IllegalArgumentException("Ruta nula");
        String s2v = toS2V(hoja);
        try {
            // Crea carpetas si no existen
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, s2v);
        } catch (IOException e) {
            throw new IllegalStateException("Error al guardar la hoja en S2V: " + e.getMessage(), e);
        }
    }

    /** Atajo: guarda en una ruta dada como String. */
    public static void saveToFile(HojaCalculo hoja, String path) {
        if (path == null) throw new IllegalArgumentException("Ruta nula");
        saveToFile(hoja, Path.of(path));
    }
}