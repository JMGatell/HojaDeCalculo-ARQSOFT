import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class PostfixEvaluator {

    public static double evaluate(List<Token> postfix, HojaCalculo hoja) {
        Deque<Double> stack = new ArrayDeque<>();

        for (Token t : postfix) {
            switch (t.getType()) {

                case NUMBER:
                    stack.push(Double.parseDouble(t.getLexeme()));
                    break;

                case CELL_REF:
                    // HojaCalculo.getCelda(...) espera una Coordenada, no un String
                    Celda celdaRef = hoja.getCelda(Coordenada.parse(t.getLexeme()));
                    Valor valorRef = celdaRef.getValor();

                    // Convertimos el valor a número de la forma más compatible posible
                    if (valorRef == null) {
                        throw new IllegalArgumentException(
                                "La celda " + t.getLexeme() + " no tiene valor");
                    }

                    try {
                        stack.push(Double.parseDouble(valorRef.toString()));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException(
                                "La celda " + t.getLexeme() + " no contiene un valor numérico");
                    }
                    break;

                case OPERATOR:
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(t.getLexeme(), a, b));
                    break;

                default:
                    throw new IllegalStateException("Token no evaluable: " + t);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Expresión postfix inválida");
        }

        return stack.pop();
    }

    private static double applyOperator(String op, double a, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("División por cero");
                return a / b;
            default:
                throw new IllegalArgumentException("Operador desconocido: " + op);
        }
    }
}