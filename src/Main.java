import java.util.List;

public class Main {

    public static void main(String[] args) {
        HojaCalculo hoja = new HojaCalculo();
        SpreadsheetController controller = new SpreadsheetController(hoja);

        controller.modificarCelda("A1", "5");
        controller.modificarCelda("B1", "hola");
        controller.modificarCelda("C1", "=A1+2"); // todavía no se evalúa, queda como #FORMULA
        controller.modificarCelda("B3", "4");

        System.out.println("A1 = " + controller.consultarValor("A1")); // 5.0
        System.out.println("B1 = " + controller.consultarValor("B1")); // hola
        System.out.println("C1 = " + controller.consultarValor("C1")); // #FORMULA (placeholder)

        List<Token> tokens = Tokenizer.tokenize("A1+2*B1");
        List<Token> postfix = PostfixConverter.toPostfix(tokens);
        double res = PostfixEvaluator.evaluate(postfix, hoja);

        System.out.println("Postfix result = " + res); // debería ser 13.0

    }
}
