import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Transmission {
  private final ProtocolConfig config;
  private ServerSocket serverSocket;
  private volatile boolean running = true;

  public Transmission(ProtocolConfig config) {
    this.config = config;
  }

  public void start() {
    int port = config.getNetwork().getPort();

    try {
      serverSocket = new ServerSocket(port);

      // Hook para cerrar el serverSocket en shutdown (Ctrl+C)
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\n\tShutdown Hook: cerrando server socket...");
        running = false;
        try {
          if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }));

      System.out.println("Servidor escuchando en " + config.getNetwork().getHost() + ":" + port + "...");

      while (running) {
        try {
          Socket clientSocket = serverSocket.accept();
          // System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

          BufferedReader in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));

          String message = in.readLine();
          System.out.println("\tMensaje recibido: " + message);

          clientSocket.close();

        } catch (java.net.SocketException se) {
          if (!running) {
            System.out.println("Server socket cerrado, terminando ciclo.");
          } else {
            se.printStackTrace();
          }
        }
      }
      System.out.println("Servidor detenido correctamente.");
      System.out.flush();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
