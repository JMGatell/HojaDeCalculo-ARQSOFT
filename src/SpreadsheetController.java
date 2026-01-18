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

        // 3) Identificar y crear contenido
        Contenido contenido = IdentificadorDeContenido.crearContenido(contenidoRaw);

        // 4) Asignar contenido a la celda (esto actualiza valor "por defecto")
        celda.setContenido(contenido);

        // 5) Si es fórmula -> registrar dependencias y evaluar
        if (contenido instanceof ContenidoFormula) {
            String expr = ((ContenidoFormula) contenido).getExpresion();

            // 5.1) Extraer dependencias directas de la fórmula y registrarlas en la hoja
            List<Token> tokens = Tokenizer.tokenize(expr);
            Set<Coordenada> deps = CalculadoraDeDependencias.extraerDependencias(tokens);
            hoja.setDependencias(coord, deps);

            if (CalculadoraDeDependencias.hayDependenciaCircular(hoja, coord)) {
            throw new IllegalArgumentException("Dependencia circular detectada");
        }

            // 5.2) Evaluar la fórmula y guardar su valor
            double resultado = FormulaService.evaluate(expr, hoja);
            celda.setValor(Valor.numero(resultado));
        } else {
            // Si deja de ser fórmula, eliminamos dependencias previas
            hoja.setDependencias(coord, Set.of());
        }

        // 6) Recalcular dependientes (Fase 3)
        CalculadoraDeDependencias.recalcularDesde(hoja, coord);
    }

    public Valor consultarValor(String coordenadaRaw) {
        Coordenada coord = Coordenada.parse(coordenadaRaw);
        Celda celda = hoja.getCelda(coord);
        if (celda == null) return Valor.texto(""); // o null, según criterio
        return celda.getValor();
    }
}
