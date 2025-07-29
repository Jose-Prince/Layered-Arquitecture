package app;

import config.ProtocolConfig;

public class Application {

  /**
   * Procesa un mensaje decodificado con Hamming y muestra el resultado final.
   *
   * @param result Resultado de la validación y decodificación
   * @param config Configuración cargada del protocolo (opcional para futura
   *               expansión)
   */
  public static void processHamming(core.ValidationResult result, ProtocolConfig config) {
    if (result.isError()) {
      if (result.isOneError()) {
        System.out.println("\tMensaje corregido: " + result.getDecodedMessage());
      } else {
        System.out.println("\tError no corregible. Se requiere retransmisión.");
        System.out.println("\tMensaje: RETRANSMITIR");
      }
    } else {
      System.out.println("\tMensaje válido: " + result.getDecodedMessage());
    }
  }
}
