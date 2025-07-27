import java.io.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    String configPath = "./protocol.yaml";
    String outputPath = "./reports/r_hamming_report.txt";
    String detailPath = "./reports/r_hamming_detail.txt";

    Scanner scanner = new Scanner(System.in);

    System.out.println("===== RECEPTOR HAMMING =====\n");
    System.out.println(
        "Se utilizará la configuración definida en el archivo: '" + new File(configPath).getAbsolutePath() + "'\n");

    // Mostrar configuración cargada
    String parityType = "par";
    boolean extended = true;

    try {
      Map<String, Object> config = new org.yaml.snakeyaml.Yaml().load(new java.io.FileInputStream(configPath));
      parityType = (String) config.getOrDefault("parity", "even");
      extended = (boolean) config.getOrDefault("extended", true);
    } catch (Exception e) {
      System.out.println("No se pudo cargar la configuración. Se usarán valores por defecto.");
    }

    System.out.println("Configuración cargada:");
    System.out.println("- Paridad: " + (parityType.equals("even") ? "par" : "impar"));
    System.out.println("- Paridad extendida: " + (extended ? "sí" : "no") + "\n");

    System.out.println("\tHint: Si deseas modificar la configuración, edita el archivo 'protocol.yaml'\n");

    System.out.println("Ingrese los datos para decodificar con Hamming:\n");

    // Solicitar trama binaria
    String msg = "";
    while (true) {
      System.out.print("Ingrese la trama binaria (solo 0s y 1s): ");
      msg = scanner.nextLine().trim();
      if (!msg.matches("[01]+")) {
        System.out.println("Entrada inválida. Solo se permiten 0s y 1s.\n");
      } else {
        break;
      }
    }

    // Solicitar cantidad total de bits
    int totalBits = 0;
    while (true) {
      System.out.print("Ingrese el total de bits: ");
      try {
        totalBits = Integer.parseInt(scanner.nextLine().trim());
        if (totalBits <= 0 || totalBits != msg.length()) {
          System.out.println("Debe coincidir con el tamaño del mensaje binario (" + msg.length() + ").\n");
        } else {
          break;
        }
      } catch (NumberFormatException e) {
        System.out.println("Entrada inválida. Ingrese un número entero positivo.\n");
      }
    }

    // Solicitar cantidad de bits de datos
    int dataBits = 0;
    while (true) {
      System.out.print("Ingrese la cantidad de bits de datos: ");
      try {
        dataBits = Integer.parseInt(scanner.nextLine().trim());
        if (dataBits <= 0 || dataBits >= totalBits) {
          System.out.println("El valor debe ser positivo y menor que el total de bits (" + totalBits + ").\n");
        } else {
          break;
        }
      } catch (NumberFormatException e) {
        System.out.println("Entrada inválida. Ingrese un número entero positivo.\n");
      }
    }

    // Procesar con el receptor
    Receiver receiver = new Receiver(msg, totalBits, dataBits, outputPath, detailPath, configPath);

    // Exportar resultados
    receiver.exportToTxt(outputPath);
    receiver.exportDetailTxt(detailPath);

    // Mostrar resumen
    String resultadoFinal = receiver.getMsgOutput();
    System.out.println("\nResultado de la decodificación:");
    System.out.println("- Mensaje final decodificado: " + resultadoFinal);
    System.out.println("- Protocolo de data: (" + totalBits + "," + dataBits + ")");

    System.out.println("\n\tReporte generado en: '" + new File(outputPath).getAbsolutePath() + "'");
    System.out
        .println("\tDetalle de los pasos seguidos, generado en: '" + new File(detailPath).getAbsolutePath() + "'");

    scanner.close();
  }
}
