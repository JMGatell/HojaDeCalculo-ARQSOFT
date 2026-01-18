import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class S2VLoader {

    private S2VLoader() {}

    /**
     * CU6 - Cargar hoja de cálculo (S2V) desde fichero.
     */
    public static HojaCalculo loadFromFile(String path) {
        if (path == null) throw new IllegalArgumentException("Ruta nula");
        try {
            String s2v = Files.readString(Path.of(path));
            return fromS2V(s2v);
        } catch (IOException e) {
            throw new IllegalStateException("Error al cargar el archivo S2V: " + e.getMessage(), e);
        }
    }

    /**
     * CU6 - Cargar hoja de cálculo (S2V) desde un String.
     * 
     * Nota: este método SOLO reconstruye contenidos. La evaluación/recalculo se hará después.
     */
    public static HojaCalculo fromS2V(String s2v) {
        if (s2v == null) throw new IllegalArgumentException("Contenido S2V nulo");

        HojaCalculo hoja = new HojaCalculo();
        String trimmed = s2v;

        // Permitir archivo vacío
        if (trimmed.isEmpty()) return hoja;

        // Separar filas (soporta \n y \r\n)
        String[] lineas = trimmed.split("\\r?\\n", -1);

        for (int i = 0; i < lineas.length; i++) {
            int fila = i + 1;
            String linea = lineas[i];

            // Cada celda separada por ';'. Usamos -1 para conservar vacías.
            String[] celdas = linea.split(";", -1);

            for (int j = 0; j < celdas.length; j++) {
                int col = j + 1;
                String rawCell = celdas[j];

                // Si está totalmente vacía, no creamos celda (equivalente a celda inexistente)
                if (rawCell == null || rawCell.isEmpty()) continue;

                Coordenada coord = new Coordenada(fila, col);
                Celda celda = hoja.obtenerOCrearCelda(coord);

                // Si es fórmula, revertimos ',' -> ';' dentro de los argumentos, si procede.
                // (Regla de guardado: ';' en argumentos se guardan como ',')
                String normalized = rawCell;
                if (normalized.startsWith("=")) {
                    normalized = "=" + normalized.substring(1).replace(',', ';');
                }

                Contenido contenido = IdentificadorDeContenido.crearContenido(normalized);
                celda.setContenido(contenido);
            }
        }
        
        RecalculoService.recalcularHoja(hoja);
        
        return hoja;
    }
}