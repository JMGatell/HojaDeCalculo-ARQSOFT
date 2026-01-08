public final class ContenidoFormula extends Contenido {
    private final String expresion; // incluye '='

    public ContenidoFormula(String expresion) { this.expresion = expresion; }

    public String getExpresion() { return expresion; }

    @Override
    public Valor getValor() {
        // Placeholder: el valor real lo calculará FormulaService en la Fase 2
        return Valor.texto("#FORMULA");
    }
}
