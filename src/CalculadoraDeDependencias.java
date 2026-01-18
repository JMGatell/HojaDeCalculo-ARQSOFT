import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CalculadoraDeDependencias {

    private CalculadoraDeDependencias() {
        // Pure Fabrication: no instanciable
    }

    /**
     * Extrae las dependencias (referencias a celdas) a partir de los tokens
     * de una fórmula.
     */
    public static Set<Coordenada> extraerDependencias(List<Token> tokens) {
        Set<Coordenada> deps = new HashSet<>();

        for (Token t : tokens) {
            if (t.getType() == TokenType.CELL_REF) {
                deps.add(Coordenada.parse(t.getLexeme()));
            }
        }

        return deps;
    }
    /**
     * Comprueba si existe una dependencia circular alcanzable desde 'inicio'.
     * Usa DFS con conjuntos de visitando/visitadas.
     */
    public static boolean hayDependenciaCircular(HojaCalculo hoja, Coordenada inicio) {
        Set<Coordenada> visitando = new HashSet<>();
        Set<Coordenada> visitadas = new HashSet<>();
        return dfsCiclo(hoja, inicio, visitando, visitadas);
    }

    private static boolean dfsCiclo(HojaCalculo hoja,
                                    Coordenada actual,
                                    Set<Coordenada> visitando,
                                    Set<Coordenada> visitadas) {
        if (visitando.contains(actual)) return true;   // ciclo encontrado
        if (visitadas.contains(actual)) return false;  // ya comprobado

        visitando.add(actual);

        for (Coordenada dep : hoja.getDependenciasDe(actual)) {
            if (dfsCiclo(hoja, dep, visitando, visitadas)) return true;
        }

        visitando.remove(actual);
        visitadas.add(actual);
        return false;
    }
}