public final class Valor {
    private final Double numero;  // null si no es número
    private final String texto;   // null si no es texto

    private Valor(Double numero, String texto) {
        this.numero = numero;
        this.texto = texto;
    }

    public static Valor numero(double n) { return new Valor(n, null); }
    public static Valor texto(String t)  { return new Valor(null, t); }

    public boolean esNumero() { return numero != null; }
    public boolean esTexto()  { return texto != null; }

    public double asDouble() {
        if (numero == null) throw new IllegalStateException("El valor no es numérico");
        return numero;
    }

    public String asString() {
        if (texto != null) return texto;
        if (numero != null) return String.valueOf(numero);
        return "";
    }

    @Override public String toString() { return asString(); }
}