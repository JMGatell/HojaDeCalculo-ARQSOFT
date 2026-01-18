import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class HojaCalculo {
    private final Map<Coordenada, Celda> celdas = new HashMap<>();

    // Grafo de dependencias (implementación):
    // - dependencias.get(X) = conjunto de celdas de las que X depende
    // - dependientes.get(X) = conjunto de celdas que dependen de X
    private final Map<Coordenada, Set<Coordenada>> dependencias = new HashMap<>();
    private final Map<Coordenada, Set<Coordenada>> dependientes = new HashMap<>();

    public Celda obtenerOCrearCelda(Coordenada coord) {
        return celdas.computeIfAbsent(coord, Celda::new);
    }

    public Celda getCelda(Coordenada coord) {
        return celdas.get(coord);
    }

    public Map<Coordenada, Celda> getCeldas() {
        return Map.copyOf(celdas);
    }

    /**
     * Devuelve (copia inmodificable) de las dependencias directas de una celda.
     */
    public Set<Coordenada> getDependenciasDe(Coordenada celda) {
        Set<Coordenada> deps = dependencias.get(celda);
        if (deps == null) return Set.of();
        return Collections.unmodifiableSet(deps);
    }

    /**
     * Devuelve (copia inmodificable) de los dependientes directos de una celda.
     */
    public Set<Coordenada> getDependientesDe(Coordenada celda) {
        Set<Coordenada> deps = dependientes.get(celda);
        if (deps == null) return Set.of();
        return Collections.unmodifiableSet(deps);
    }

    /**
     * Sustituye las dependencias directas de 'destino' por 'nuevasDependencias',
     * actualizando también el mapa inverso de dependientes.
     */
    public void setDependencias(Coordenada destino, Set<Coordenada> nuevasDependencias) {
        if (destino == null) throw new IllegalArgumentException("Destino nulo");
        if (nuevasDependencias == null) nuevasDependencias = Set.of();

        // 1) Eliminar enlaces inversos antiguos: A -> destino (para cada A en antiguas)
        Set<Coordenada> antiguas = dependencias.get(destino);
        if (antiguas != null) {
            for (Coordenada base : antiguas) {
                Set<Coordenada> deps = dependientes.get(base);
                if (deps != null) {
                    deps.remove(destino);
                    if (deps.isEmpty()) dependientes.remove(base);
                }
            }
        }

        // 2) Guardar dependencias directas nuevas
        if (nuevasDependencias.isEmpty()) {
            dependencias.remove(destino);
        } else {
            dependencias.put(destino, new HashSet<>(nuevasDependencias));
        }

        // 3) Crear enlaces inversos nuevos: base -> destino
        for (Coordenada base : nuevasDependencias) {
            dependientes.computeIfAbsent(base, k -> new HashSet<>()).add(destino);
        }
    }
}
