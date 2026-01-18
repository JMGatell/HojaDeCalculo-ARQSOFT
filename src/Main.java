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

        // Caso CON ciclo (debe lanzar excepción)
        controller.modificarCelda("A1", "=B1+1");

    }
}
