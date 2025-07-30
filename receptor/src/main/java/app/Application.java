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
      System.out.println("\tMensaje válido: " + binToAscii(result.getDecodedMessage(), config.getBits_per_char()));
    }
  }

  /**
   * Convierte una cadena binaria en su representación ASCII.
   *
   * Esta función toma una cadena compuesta por bits (por ejemplo,
   * "0100100001100101")
   * y la divide en bloques del tamaño especificado por `bitsPerChar` para
   * convertir
   * cada bloque en un carácter ASCII correspondiente.
   *
   * @param binary      Cadena binaria a convertir.
   * @param bitsPerChar Número de bits que representa cada carácter (usualmente
   *                    8).
   * @return Cadena de texto resultante en formato ASCII.
   */
  public static String binToAscii(String binary, int bitsPerChar) {
    StringBuilder mensaje = new StringBuilder();
    for (int i = 0; i < binary.length(); i += bitsPerChar) {
      String byteStr = binary.substring(i, i + bitsPerChar);
      int charCode = Integer.parseInt(byteStr, 2);
      mensaje.append((char) charCode);
    }
    return mensaje.toString();
  }

}
