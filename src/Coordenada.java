import java.util.Objects;

public final class Coordenada {
    private final int fila;     // 1..n
    private final int columna;  // 1..n (A=1, B=2, ...)

    public Coordenada(int fila, int columna) {
        if (fila <= 0 || columna <= 0) throw new IllegalArgumentException("Fila/columna inválidas");
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    public static Coordenada parse(String raw) {
        if (raw == null) throw new IllegalArgumentException("Coordenada nula");
        String s = raw.trim().toUpperCase();

        if (!s.matches("[A-Z]+[0-9]+")) {
            throw new IllegalArgumentException("Formato de coordenada inválido: " + raw);
        }

        int i = 0;
        while (i < s.length() && Character.isLetter(s.charAt(i))) i++;

        String colStr = s.substring(0, i);
        String filaStr = s.substring(i);

        int fila = Integer.parseInt(filaStr);
        int col = 0;
        for (char c : colStr.toCharArray()) {
            col = col * 26 + (c - 'A' + 1);
        }

        return new Coordenada(fila, col);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordenada)) return false;
        Coordenada that = (Coordenada) o;
        return fila == that.fila && columna == that.columna;
    }

    @Override public int hashCode() { return Objects.hash(fila, columna); }

    @Override public String toString() {
        // opcional: convertir columna numérica a letras
        return "(" + fila + "," + columna + ")";
    }
}