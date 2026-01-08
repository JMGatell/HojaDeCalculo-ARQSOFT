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

        // 5) Si es fórmula -> evaluar (Fase 2)
        if (contenido instanceof ContenidoFormula) {
            // Por ahora: dejamos el placeholder en la celda (ContenidoFormula.getValor() da "#FORMULA")
            // En la siguiente fase: FormulaService.evaluarFormula(celda, hoja) y celda.setValor(resultado)
        }

        // 6) Recalcular dependientes (Fase 3)
        // En la siguiente fase: CalculadoraDeDependencias.recalcularDesde(celda, hoja)
    }

    public Valor consultarValor(String coordenadaRaw) {
        Coordenada coord = Coordenada.parse(coordenadaRaw);
        Celda celda = hoja.getCelda(coord);
        if (celda == null) return Valor.texto(""); // o null, según criterio
        return celda.getValor();
    }
}
