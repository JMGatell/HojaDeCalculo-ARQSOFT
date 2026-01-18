import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RecalculoService {

    private RecalculoService() {}

    /** Recalcula todas las fórmulas de la hoja (registra deps, detecta ciclos, evalúa). */
    public static void recalcularHoja(HojaCalculo hoja) {
        if (hoja == null) throw new IllegalArgumentException("Hoja nula");

        // 1) Registrar dependencias de TODAS las fórmulas
        Set<Coordenada> formulas = new HashSet<>();

        for (Map.Entry<Coordenada, Celda> e : hoja.getCeldas().entrySet()) {
            Coordenada coord = e.getKey();
            Celda celda = e.getValue();

            Contenido cont = celda.getContenido();
            if (cont instanceof ContenidoFormula) {
                formulas.add(coord);

                String expr = ((ContenidoFormula) cont).getExpresion(); // SIN '='
                Set<Coordenada> deps = extraerDependencias(expr);
                hoja.setDependencias(coord, deps);
            } else {
                // si no es fórmula, nos aseguramos de que no deje deps viejas
                hoja.setDependencias(coord, Set.of());
            }
        }

        // 2) Detectar ciclos (si hay, lanzamos error)
        for (Coordenada f : formulas) {
            if (CalculadoraDeDependencias.hayDependenciaCircular(hoja, f)) {
                throw new IllegalArgumentException("Dependencia circular detectada");
            }
        }

        // 3) Evaluar fórmulas en orden topológico (solo nodos fórmula)
        evaluarFormulasEnOrden(hoja, formulas);
    }

    private static Set<Coordenada> extraerDependencias(String expr) {
        Set<Coordenada> deps = new HashSet<>();
        for (Token t : Tokenizer.tokenize(expr)) {
            if (t.getType() == TokenType.CELL_REF) {
                deps.add(Coordenada.parse(t.getLexeme()));
            }
        }
        return deps;
    }

    private static void evaluarFormulasEnOrden(HojaCalculo hoja, Set<Coordenada> formulas) {
        // indegree SOLO entre fórmulas
        Map<Coordenada, Integer> indeg = new HashMap<>();
        for (Coordenada f : formulas) indeg.put(f, 0);

        for (Coordenada f : formulas) {
            for (Coordenada dep : hoja.getDependenciasDe(f)) {
                if (formulas.contains(dep)) {
                    indeg.put(f, indeg.get(f) + 1);
                }
            }
        }

        Deque<Coordenada> q = new ArrayDeque<>();
        for (Map.Entry<Coordenada, Integer> e : indeg.entrySet()) {
            if (e.getValue() == 0) q.add(e.getKey());
        }

        int procesadas = 0;

        while (!q.isEmpty()) {
            Coordenada f = q.removeFirst();
            procesadas++;

            Celda c = hoja.getCelda(f);
            if (c != null && c.getContenido() instanceof ContenidoFormula) {
                String expr = ((ContenidoFormula) c.getContenido()).getExpresion();
                double res = FormulaService.evaluate(expr, hoja);
                c.setValor(Valor.numero(res));
            }

            // Disminuir indegree de los que dependen de f (solo si también son fórmula)
            for (Coordenada depd : hoja.getDependientesDe(f)) {
                if (!formulas.contains(depd)) continue;
                indeg.put(depd, indeg.get(depd) - 1);
                if (indeg.get(depd) == 0) q.add(depd);
            }
        }

        if (procesadas != formulas.size()) {
            // Si esto pasa, hay un ciclo que no detectamos por alguna razón
            throw new IllegalStateException("Recalculo incompleto (posible ciclo)");
        }
    }
}