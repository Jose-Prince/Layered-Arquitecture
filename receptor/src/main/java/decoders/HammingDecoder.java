package decoders;

import java.util.List;
import java.util.Map;

public class HammingDecoder {
  // Entradas
  private String message;
  private int dataBits;
  private int totalBits;
  private boolean isExtended;
  private boolean isEvenParity;

  // Datos auxiliares
  private int redundancyBits;
  private List<Integer> posRedundancyBits;
  private Map<Integer, List<Integer>> redundancyCoverageMap;

  // Bits procesados
  private List<Map.Entry<Integer, String>> msgBitsReceived;
  private List<Map.Entry<Integer, String>> msgBitsCalculated;

  // Resultado
  private core.ValidationResult validationResult;
  private Integer extendedParityBit;

  // Documentación
  private List<String> detailLines;
  private List<String> typeBit = List.of("d", "r", "rg", "alg", "len");

  /**
   * Obtener el mensaje binario recibido
   */
  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Obtener la cantidad de bits que son correspondiente a `data`
   */
  public int getDataBits() {
    return this.dataBits;
  }

  public void setDataBits(int dataBits) {
    this.dataBits = dataBits;
  }

  /**
   * Obtener la cantidad de bits del mensaje recibido
   */
  public int getTotalBits() {
    return this.totalBits;
  }

  public void setTotalBits(int totalBits) {
    this.totalBits = totalBits;
  }

  /**
   * Obtener booleano que indica si usa bit de paridad global
   */
  public boolean isExtended() {
    return this.isExtended;
  }

  public void setExtended(boolean isExtended) {
    this.isExtended = isExtended;
  }

  /**
   * Obtener booleano que indica si usa paridad par
   */
  public boolean isEvenParity() {
    return this.isEvenParity;
  }

  public void setEvenParity(boolean isEvenParity) {
    this.isEvenParity = isEvenParity;
  }

  /**
   * Obtener la cantidad de bits de redundancia
   */
  public int getRedundancyBits() {
    return this.redundancyBits;
  }

  public void setRedundancyBits(int redundancyBits) {
    this.redundancyBits = redundancyBits;
  }

  /**
   * Obtener las posiciones de los bits de redundancia
   */
  public List<Integer> getPosRedundancyBits() {
    return this.posRedundancyBits;
  }

  public void setPosRedundancyBits(List<Integer> posRedundancyBits) {
    this.posRedundancyBits = posRedundancyBits;
  }

  /**
   * Obtener el mapa de posicion de bits de redundancia - posicion de bits que
   * cubre ese bit de redundancia
   * ej: 1:[1,3,5,7] el bit de redundancia en posicion 1 se compone por el bit que
   * esta en la posicion 1,3,5,7
   * esto sirve para determinar el valor del bit en la posicion 1 (segun la
   * paridad)
   */
  public Map<Integer, List<Integer>> getRedundancyCoverageMap() {
    return this.redundancyCoverageMap;
  }

  public void setRedundancyCoverageMap(Map<Integer, List<Integer>> redundancyCoverageMap) {
    this.redundancyCoverageMap = redundancyCoverageMap;
  }

  /**
   * Mapeo del mensaje recibido con su tipo de bit
   */
  public List<Map.Entry<Integer, String>> getMsgBitsReceived() {
    return msgBitsReceived;
  }

  public void setMsgBitsReceived(List<Map.Entry<Integer, String>> msgBitsReceived) {
    this.msgBitsReceived = msgBitsReceived;
  }

  /**
   * Mapeo del mensaje recibido recalculado con su tipo de bit
   */
  public List<Map.Entry<Integer, String>> getMsgBitsCalculated() {
    return msgBitsCalculated;
  }

  public void setMsgBitsCalculated(List<Map.Entry<Integer, String>> msgBitsCalculated) {
    this.msgBitsCalculated = msgBitsCalculated;
  }

  /**
   * Obtener el objeto resultado de la decodificación dado la validación (hay
   * error, donde esta el error y mensaje decodificado)
   */
  public core.ValidationResult getValidationResult() {
    return this.validationResult;
  }

  public void setValidationResult(core.ValidationResult validationResult) {
    this.validationResult = validationResult;
  }

  /**
   * Obtener el valor del bit de redundancia global recalculado
   */
  public Integer getExtendedParityBit() {
    return this.extendedParityBit;
  }

  public void setExtendedParityBit(Integer extendedParityBit) {
    this.extendedParityBit = extendedParityBit;
  }

  /**
   * Obtener el detalle de las lineas que se guardan en el proceso de
   * decodificación
   */
  public List<String> getDetailLines() {
    return this.detailLines;
  }

  public void setDetailLines(List<String> detailLines) {
    this.detailLines = detailLines;
  }

  /**
   * Obtener los tipos de bits
   */
  public List<String> getTypeBit() {
    return this.typeBit;
  }

  public void setTypeBit(List<String> typeBit) {
    this.typeBit = typeBit;
  }

}