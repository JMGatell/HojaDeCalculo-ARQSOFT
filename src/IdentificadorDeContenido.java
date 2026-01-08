public final class IdentificadorDeContenido {

    public static Contenido crearContenido(String raw) {
        if (raw == null) return new ContenidoTexto("");

        String s = raw.trim();

        if (s.startsWith("=")) {
            return new ContenidoFormula(s);
        }

        try {
            double n = Double.parseDouble(s);
            return new ContenidoNumerico(n);
        } catch (NumberFormatException e) {
            return new ContenidoTexto(s);
        }
    }
}
