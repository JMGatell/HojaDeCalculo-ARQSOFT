public final class Valor {

    private final Double numero; // null si es texto
    private final String texto;  // null si es número

    private Valor(Double numero, String texto) {
        this.numero = numero;
        this.texto = texto;
    }

    public static Valor numero(double n) {
        return new Valor(n, null);
    }

    public static Valor texto(String s) {
        if (s == null) s = "";
        return new Valor(null, s);
    }

    public boolean esNumero() {
        return numero != null;
    }

    public double comoNumero() {
        if (numero == null) throw new IllegalStateException("El valor no es numérico");
        return numero;
    }

    public String comoTexto() {
        if (texto == null) return Double.toString(numero);
        return texto;
    }

    @Override
    public String toString() {
        return (texto != null) ? texto : Double.toString(numero);
    }
}
