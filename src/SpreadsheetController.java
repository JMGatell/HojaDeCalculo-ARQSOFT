import java.util.List;
import java.util.Set;

public final class SpreadsheetController {

    private final HojaCalculo hoja;

    public SpreadsheetController(HojaCalculo hoja) {
        if (hoja == null) throw new IllegalArgumentException("HojaCalculo nula");
        this.hoja = hoja;
    }

    /**
     * CU1 - Modificar contenido de una celda
     */
    public void modificarCelda(String coordenadaRaw, String contenidoRaw) {
        // 1) Validar/parsear coordenada
        Coordenada coord = Coordenada.parse(coordenadaRaw);

        // 2) Obtener o crear celda
        Celda celda = hoja.obtenerOCrearCelda(coord);

        // Guardar estado previo para poder hacer rollback si hay error
        Contenido contenidoPrevio = celda.getContenido();
        Valor valorPrevio = celda.getValor();
        Set<Coordenada> depsPrevias = Set.copyOf(hoja.getDependenciasDe(coord));

        try {
            // 3) Identificar y crear contenido
            Contenido contenido = IdentificadorDeContenido.crearContenido(contenidoRaw);

            // 4) Si es fórmula -> registrar dependencias, validar y evaluar ANTES de aplicar cambios
            if (contenido instanceof ContenidoFormula) {
                String expr = ((ContenidoFormula) contenido).getExpresion();

                // 4.1) Extraer dependencias directas de la fórmula y registrarlas en la hoja
                List<Token> tokens = Tokenizer.tokenize(expr);
                Set<Coordenada> deps = CalculadoraDeDependencias.extraerDependencias(tokens);
                hoja.setDependencias(coord, deps);

                // 4.2) Detectar dependencia circular
                if (CalculadoraDeDependencias.hayDependenciaCircular(hoja, coord)) {
                    throw new IllegalArgumentException("Dependencia circular detectada");
                }

                // 4.3) Evaluar la fórmula
                double resultado = FormulaService.evaluate(expr, hoja);

                // 4.4) Aplicar cambios solo si todo ha ido bien
                celda.setContenido(contenido);
                celda.setValor(Valor.numero(resultado));

            } else {
                // Si deja de ser fórmula, eliminamos dependencias previas
                hoja.setDependencias(coord, Set.of());

                // Aplicar contenido a la celda (esto actualiza valor "por defecto")
                celda.setContenido(contenido);
            }

            // 5) Recalcular dependientes (Fase 3)
            CalculadoraDeDependencias.recalcularDesde(hoja, coord);

        } catch (RuntimeException ex) {
            // Rollback: restaurar dependencias y estado previo de la celda
            hoja.setDependencias(coord, depsPrevias);
            celda.setContenido(contenidoPrevio);
            celda.setValor(valorPrevio);
            throw ex;
        }
    }

    public Valor consultarValor(String coordenadaRaw) {
        Coordenada coord = Coordenada.parse(coordenadaRaw);
        Celda celda = hoja.getCelda(coord);
        if (celda == null) return Valor.texto(""); // o null, según criterio
        return celda.getValor();
    }
}
