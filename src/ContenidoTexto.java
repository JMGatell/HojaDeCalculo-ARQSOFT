public final class ContenidoTexto extends Contenido {
    private final String texto;

    public ContenidoTexto(String texto) { this.texto = texto; }

    @Override
    public Valor getValor() { return Valor.texto(texto); }

    public String getTexto() { return texto; }
}
