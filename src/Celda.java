public final class Celda {
    private final Coordenada coordenada;
    private Contenido contenido;
    private Valor valor;

    public Celda(Coordenada coordenada) {
        this.coordenada = coordenada;
        this.contenido = new ContenidoTexto(""); // por defecto
        this.valor = this.contenido.getValor();
    }

    public Coordenada getCoordenada() { return coordenada; }

    public Contenido getContenido() { return contenido; }
    public Valor getValor() { return valor; }

    public void setContenido(Contenido contenido) {
        if (contenido == null) throw new IllegalArgumentException("Contenido nulo");
        this.contenido = contenido;
        this.valor = contenido.getValor(); // en fórmula se actualizará luego con FormulaService
    }

    public void setValor(Valor valor) {
        if (valor == null) throw new IllegalArgumentException("Valor nulo");
        this.valor = valor;
    }
}
