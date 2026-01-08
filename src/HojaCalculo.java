import java.util.HashMap;
import java.util.Map;

public final class HojaCalculo {
    private final Map<Coordenada, Celda> celdas = new HashMap<>();

    public Celda obtenerOCrearCelda(Coordenada coord) {
        return celdas.computeIfAbsent(coord, Celda::new);
    }

    public Celda getCelda(Coordenada coord) {
        return celdas.get(coord);
    }

    public Map<Coordenada, Celda> getCeldas() {
        return Map.copyOf(celdas);
    }
}
