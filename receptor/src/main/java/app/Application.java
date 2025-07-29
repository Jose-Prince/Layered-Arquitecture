package app;

import config.ProtocolConfig;
import core.ValidationResult;

public class Application {

  /**
   * Procesa el resultado de la decodificación de un mensaje y muestra el estado
   * final.
   *
   * Dependiendo del resultado, imprime:
   * - El mensaje corregido si hubo un solo error corregible.
   * - Una advertencia de retransmisión si el error no es corregible.
   * - El mensaje válido si no se detectaron errores.
   *
   * @param result Objeto ValidationResult que contiene el estado del mensaje
   *               decodificado
   * @param config Configuración del protocolo (actualmente no utilizada, pero
   *               disponible para futuras extensiones)
   */
  public static void process(ValidationResult result, ProtocolConfig config) {
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
