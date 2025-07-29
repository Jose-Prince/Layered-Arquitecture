package app;

import config.ProtocolConfig;
import core.ValidationResult;
import decoders.HammingDecoder;

public class Presentation {

  /**
   * Decodifica un mensaje binario usando HammingDecoder según la configuración.
   * 
   * @param message Mensaje binario recibido (incluye bit de algoritmo y
   *                configuración)
   * @param config  Configuración cargada del protocolo
   * @return Resultado de la validación y decodificación
   */

  public static ValidationResult decodeHammingMessage(String message, ProtocolConfig config) {
    boolean isExtended = config.isExtended();
    boolean isEven = config.isParityEven();
    int bitsConfig = config.getHamming().getBits_configuration();
    boolean showLog = false;

    HammingDecoder decoder = new HammingDecoder(message, bitsConfig, isExtended, isEven);

    if (showLog) {
      for (String line : decoder.getDetailLines()) {
        System.out.println(line);
      }
    }

    return decoder.getValidationResult();
  }

}
