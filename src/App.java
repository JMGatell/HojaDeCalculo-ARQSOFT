import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public final class App {

    private HojaCalculo hoja;
    private SpreadsheetController controller;

    public App() {
        this.hoja = new HojaCalculo();
        this.controller = new SpreadsheetController(hoja);
    }

    public void run() {
        System.out.println("ARQSOFT - Hoja de cálculo (UI textual)");
        System.out.println("Comandos: RF <path> | C | E <coord> <contenido> | L <path.s2v> | S <path.s2v>");
        System.out.println("(Ctrl+D para salir)");
        imprimirHoja();

        Scanner console = new Scanner(System.in);
        processScanner(console);
        System.out.println("Fin.");
    }

    private void processScanner(Scanner sc) {
        while (sc.hasNextLine()) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;

            line = line.trim();
            if (line.isEmpty()) continue;

            try {
                ejecutarLinea(line);
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            // requisito: imprimir hoja tras cada comando
            imprimirHoja();
        }
    }

    private void ejecutarLinea(String line) {
        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toUpperCase();
        String rest = (parts.length > 1) ? parts[1].trim() : "";

        switch (cmd) {
            case "RF": ejecutarRF(rest); break;
            case "C":  ejecutarC(rest);  break;
            case "E":  ejecutarE(rest);  break;
            case "L":  ejecutarL(rest);  break;
            case "S":  ejecutarS(rest);  break;
            default: throw new IllegalArgumentException("Comando desconocido: " + cmd);
        }
    }

    private void ejecutarRF(String rest) {
        if (rest.isEmpty()) throw new IllegalArgumentException("Uso: RF <text file pathname>");
        String path = rest.split("\\s+", 2)[0].trim();
        try {
            Scanner fileScanner = new Scanner(new FileInputStream(path));
            processScanner(fileScanner);
            fileScanner.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("No se puede abrir el fichero: " + path);
        }
    }

    private void ejecutarC(String rest) {
        if (!rest.isEmpty()) throw new IllegalArgumentException("Uso: C");
        this.hoja = new HojaCalculo();
        this.controller = new SpreadsheetController(this.hoja);
    }

    private void ejecutarE(String rest) {
        if (rest.isEmpty()) throw new IllegalArgumentException("Uso: E <cell coordinate> <new cell content>");
        String[] p = rest.split("\\s+", 2);
        if (p.length < 2) throw new IllegalArgumentException("Uso: E <cell coordinate> <new cell content>");
        controller.modificarCelda(p[0].trim(), p[1]);
    }

    private void ejecutarL(String rest) {
        if (rest.isEmpty()) throw new IllegalArgumentException("Uso: L <SV2 file pathname>");
        String path = rest.split("\\s+", 2)[0].trim();
        this.hoja = S2VLoader.loadFromFile(path);
        this.controller = new SpreadsheetController(this.hoja);
    }

    private void ejecutarS(String rest) {
        if (rest.isEmpty()) throw new IllegalArgumentException("Uso: S <SV2 file pathname>");
        String path = rest.split("\\s+", 2)[0].trim();
        S2VSerializer.saveToFile(hoja, path);
    }

    private void imprimirHoja() {
        Map<Coordenada, Celda> mapa = hoja.getCeldas();
        if (mapa.isEmpty()) {
            System.out.println("(hoja vacía)");
            return;
        }

        List<Coordenada> coords = new ArrayList<>(mapa.keySet());
        coords.sort(Comparator.comparingInt(Coordenada::getFila).thenComparingInt(Coordenada::getColumna));

        System.out.println("--- HOJA ---");
        for (Coordenada c : coords) {
            Celda celda = mapa.get(c);
            String coordStr = toA1(c);

            String contenidoStr = contenidoComoString(celda);
            String valorStr = (celda.getValor() == null) ? "" : celda.getValor().toString();

            System.out.println(coordStr + " | contenido=" + contenidoStr + " | valor=" + valorStr);
        }
        System.out.println("-----------");
    }

    private static String contenidoComoString(Celda celda) {
        Contenido cont = celda.getContenido();
        if (cont instanceof ContenidoFormula) {
            String expr = ((ContenidoFormula) cont).getExpresion();
            return (expr != null && expr.startsWith("=")) ? expr : ("=" + expr);
        }
        Valor v = cont.getValor();
        return v == null ? "" : v.toString();
    }

    private static String toA1(Coordenada c) {
        int col = c.getColumna();
        StringBuilder sb = new StringBuilder();
        while (col > 0) {
            int rem = (col - 1) % 26;
            sb.insert(0, (char) ('A' + rem));
            col = (col - 1) / 26;
        }
        return sb.toString() + c.getFila();
    }
}