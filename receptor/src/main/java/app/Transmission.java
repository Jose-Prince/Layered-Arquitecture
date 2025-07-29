package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import config.ProtocolConfig;

public class Transmission {
  private final ProtocolConfig config;
  private ServerSocket serverSocket;
  private volatile boolean running = true;
  private final MessageListener listener;

  // Constructor
  public Transmission(ProtocolConfig config, MessageListener listener) {
    this.config = config;
    this.listener = listener;
  }

  /**
   * Inicia el servidor para escuchar conexiones entrantes.
   * Al recibir un mensaje, lo envía al listener definido.
   * Incluye un hook para cerrar el socket de forma segura al finalizar la
   * ejecución.
   */
  public void start() {
    int port = config.getNetwork().getPort();

    try {
      serverSocket = new ServerSocket(port);

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
        try (Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

          String message = in.readLine();

          if (listener != null) {
            listener.onMessageReceived(message);
          }

        } catch (java.net.SocketException se) {
          if (!running) {
            System.out.println("Server socket cerrado, terminando ciclo.");
          } else {
            se.printStackTrace();
          }
        }
      }

      System.out.println("Servidor detenido correctamente.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
