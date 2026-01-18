import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<Token> tokenize(String formula) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            // Ignorar espacios
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Número
            if (Character.isDigit(c)) {
                int start = i;
                while (i < formula.length() &&
                       (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    i++;
                }
                tokens.add(new Token(TokenType.NUMBER, formula.substring(start, i)));
                continue;
            }

            // Referencia a celda (A1, B12...)
            if (Character.isLetter(c)) {
                int start = i;
                while (i < formula.length() && Character.isLetter(formula.charAt(i))) i++;
                while (i < formula.length() && Character.isDigit(formula.charAt(i))) i++;
                tokens.add(new Token(TokenType.CELL_REF, formula.substring(start, i)));
                continue;
            }

            // Operadores
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                i++;
                continue;
            }

            // Paréntesis
            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                i++;
                continue;
            }

            if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")"));
                i++;
                continue;
            }

            throw new IllegalArgumentException("Carácter inválido en fórmula: " + c);
        }

        return tokens;
    }
}