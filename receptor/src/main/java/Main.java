import app.Application;
import app.Presentation;
import app.Transmission;
import config.ConfigLoader;
import config.ProtocolConfig;
import core.ValidationResult;

public class Main {
  public static void main(String[] args) {
    try {
      ProtocolConfig config = ConfigLoader.load("../protocol.yaml");

      System.out.println("Configuración de protocolo:");
      System.out.println("\tIs Parity Even: " + config.isParityEven());
      System.out.println("\tExtended: " + config.isExtended());
      System.out.println("\tBits per char: " + config.getBits_per_char());

      System.out.println("Network:");
      System.out.println("\tHost: " + config.getNetwork().getHost());
      System.out.println("\tPort: " + config.getNetwork().getPort());

      System.out.println("Algorithms:");
      System.out.println("\tHamming bit value: " + config.getAlgorithms().getHamming());
      System.out.println("\tFletcher bit value: " + config.getAlgorithms().getFletcher());

      System.out.println("Hamming:");
      System.out.println("\tBits configuration(data): " + config.getHamming().getBits_configuration());

      Transmission transmission = new Transmission(config, message -> {
        System.out.println("\nMensaje recibido: " + message);
        ValidationResult result = Presentation.decodeMessage(message, config);
        Application.process(result, config);
      });

      transmission.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
