import java.util.List;

public class FormulaService {

    public static double evaluate(String expression, HojaCalculo hoja) {
        // expression llega SIN '='
        List<Token> tokens = Tokenizer.tokenize(expression);
        List<Token> postfix = PostfixConverter.toPostfix(tokens);
        return PostfixEvaluator.evaluate(postfix, hoja);
    }
}