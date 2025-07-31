package app;

import config.ProtocolConfig;
import core.ValidationResult;
import decoders.FletcherDecoder;
import decoders.HammingDecoder;
import core.Report;

public class Presentation {

  /**
   * Decodifica un mensaje recibido basándose en el algoritmo especificado en el
   * primer bit.
   *
   * @param message Mensaje binario recibido (incluye bit de selección de
   *                algoritmo)
   * @param config  Configuración del protocolo cargada desde el archivo YAML
   * @return Objeto ValidationResult con la información de validación y
   *         decodificación
   */
  public static ValidationResult decodeMessage(String message, ProtocolConfig config, Report report) {

    report.setReceivedMessage(message);
    int algorithmBit = Character.getNumericValue(message.charAt(0));

    if (algorithmBit == config.getAlgorithms().getHamming()) {
      report.setDetectedAlgorithm("hamming");
      return decodeHammingMessage(message, config, false, report);
    }

    // Agregar otro algoritmo
    if (algorithmBit == config.getAlgorithms().getFletcher()) {
      report.setDetectedAlgorithm("fletcher");
      FletcherDecoder decoder = new FletcherDecoder(message, 16);
      return decoder.DecodeMessage();
    }

    return new ValidationResult(true, -1, false, "RETRANSMITIR");
  }

  /**
   * Decodifica un mensaje utilizando el algoritmo de Hamming según la
   * configuración proporcionada.
   * 
   * Si `showLog` es true, imprime en consola los pasos internos del proceso de
   * decodificación.
   *
   * @param message Mensaje binario recibido a procesar
   * @param config  Configuración del protocolo con parámetros de paridad y bits
   *                de datos
   * @param showLog Indica si se debe mostrar el log detallado del proceso
   * @return Objeto ValidationResult con el resultado de la decodificación
   */
  private static ValidationResult decodeHammingMessage(String message, ProtocolConfig config, boolean showLog,
      Report report) {
    HammingDecoder decoder = new HammingDecoder(
        message,
        config.getHamming().getBits_configuration(),
        config.isExtended(),
        config.isParityEven());

    if (showLog) {
      for (String line : decoder.getDetailLines()) {
        System.out.println(line);
      }
    }

    ValidationResult result = decoder.getValidationResult();

    report.setDataBitsCount(decoder.getDataBits());
    report.setDecodedBitMessage(result.getDecodedMessage());
    report.setErrorDetected(result.isError());
    report.isOneError(result.isOneError());
    if (result.getDecodedMessage() != null) {
      report.setErrorResolved(true);
    } else {
      report.setErrorResolved(false);
    }

    return result;
  }
}
