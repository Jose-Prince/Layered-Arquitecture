import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
  public static void main(String[] args) {
    String host = "127.0.0.1";
    int port = 60000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Servidor escuchando en puerto " + port + "...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

        // Leer mensaje enviado por el cliente
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));

        String message = in.readLine(); // lee hasta un salto de línea o fin de flujo
        System.out.println("Mensaje recibido: " + message);

        clientSocket.close(); // cerrar conexión con ese cliente
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}