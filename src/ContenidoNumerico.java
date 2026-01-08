public final class ContenidoNumerico extends Contenido {

    private final double numero;

    public ContenidoNumerico(double numero) {
        this.numero = numero;
    }

    @Override
    public Valor getValor() {
        return Valor.numero(numero);
    }

    public double getNumero() {
        return numero;
    }
}