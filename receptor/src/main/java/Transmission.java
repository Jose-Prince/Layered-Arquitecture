import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Transmission {
  private final ProtocolConfig config;

  public Transmission(ProtocolConfig config) {
    this.config = config;
  }

  public void start() {
    int port = config.getNetwork().getPort();

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Servidor escuchando en " + config.getNetwork().getHost() + ":" + port + "...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));

        String message = in.readLine();
        System.out.println("Mensaje recibido: " + message);

        // ✅ Mostrar también la configuración actual
        System.out.println("Usando configuración:");
        System.out.println("  Parity: " + config.getParity());
        System.out.println("  Extended: " + config.isExtended());
        System.out.println("  Bits per char: " + config.getBits_per_char());

        clientSocket.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
