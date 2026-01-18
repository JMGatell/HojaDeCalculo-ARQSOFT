import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        HojaCalculo hoja = new HojaCalculo();
        SpreadsheetController controller = new SpreadsheetController(hoja);

        // Caso SIN ciclo (debe funcionar)
        controller.modificarCelda("A1", "1");
        controller.modificarCelda("B1", "=A1+1");
        System.out.println("B1 = " + controller.consultarValor("B1"));

        try {
            controller.modificarCelda("A1", "=B1+1");
        } catch (IllegalArgumentException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        Contenido c = IdentificadorDeContenido.crearContenido("=A1+2");
        System.out.println(((ContenidoFormula)c).getExpresion());

        controller.modificarCelda("A1", "5");
        controller.modificarCelda("B1", "=A1+2");
        System.out.println("B1 = " + controller.consultarValor("B1")); // 7

        controller.modificarCelda("A1", "10");
        System.out.println("B1 = " + controller.consultarValor("B1")); // 12  (si recalcula, está bien)

        String s2v = S2VSerializer.toS2V(hoja);
        System.out.println("S2V:\n" + s2v);

        S2VSerializer.saveToFile(hoja, "output.s2v");
        System.out.println("Guardado en output.s2v");

        HojaCalculo hoja2 = S2VLoader.loadFromFile("output.s2v");
        SpreadsheetController c2 = new SpreadsheetController(hoja2);

        System.out.println("B1 = " + c2.consultarValor("B1")); // debe ser 12.0
    }
}
