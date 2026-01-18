import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class PostfixConverter {

    public static List<Token> toPostfix(List<Token> infix) {
        List<Token> output = new ArrayList<>();
        Deque<Token> opStack = new ArrayDeque<>();

        for (Token t : infix) {
            switch (t.getType()) {
                case NUMBER:
                case CELL_REF:
                    output.add(t);
                    break;

                case OPERATOR:
                    while (!opStack.isEmpty()
                            && opStack.peek().getType() == TokenType.OPERATOR
                            && precedence(opStack.peek()) >= precedence(t)) {
                        output.add(opStack.pop());
                    }
                    opStack.push(t);
                    break;

                case LPAREN:
                    opStack.push(t);
                    break;

                case RPAREN:
                    while (!opStack.isEmpty() && opStack.peek().getType() != TokenType.LPAREN) {
                        output.add(opStack.pop());
                    }
                    if (opStack.isEmpty() || opStack.peek().getType() != TokenType.LPAREN) {
                        throw new IllegalArgumentException("Paréntesis no balanceados");
                    }
                    opStack.pop(); // quitar '('
                    break;

                default:
                    throw new IllegalArgumentException("Token no soportado: " + t);
            }
        }

        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN) {
                throw new IllegalArgumentException("Paréntesis no balanceados");
            }
            output.add(top);
        }

        return output;
    }

    private static int precedence(Token opToken) {
        String op = opToken.getLexeme();
        if (op.equals("*") || op.equals("/")) return 2;
        if (op.equals("+") || op.equals("-")) return 1;
        return 0;
    }
}