package decoders;

import java.util.List;
import java.util.Map;

public class HammingDecoder {
  // Entradas
  private String message;
  private int bitsConfiguration;
  private boolean isExtended;
  private boolean isEvenParity;
  private int dataBits;
  private int totalBits;

  // Datos auxiliares
  private int redundancyBits;
  private int maxPosition;
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
  private List<String> typeBit = List.of("d", "r", "rg", "alg", "conf");

  /*
   * Constructor de la clase Receiver
   */
  public HammingDecoder(String message, int bitsConfiguration, boolean isExtended, boolean isEvenParity) {
    this.message = message;
    this.bitsConfiguration = bitsConfiguration;
    this.isExtended = isExtended;
    this.isEvenParity = isEvenParity;
    this.totalBits = message.length();

    // datos calculados
    this.dataBits = extractDataBits();
    this.maxPosition = this.isExtended ? this.totalBits - 1 : this.totalBits;
    this.redundancyBits = calculateRedundancyBits();
    this.posRedundancyBits = calculatePositionRedundancyBits();

  }

  /**
   * Obtener la cantidad de data bits dado el valor apartado para representar los
   * bits de data
   */
  private int extractDataBits() {
    String bits = message.substring(1, 1 + this.bitsConfiguration);
    return Integer.parseInt(bits, 2);
  }

  /*
   * Método para saber cuantos bits de redundancia vamos a tener
   */
  private int calculateRedundancyBits() {
    int r = 0;
    while (Math.pow(2, r) < (this.dataBits + r + 1)) {
      r++;
    }
    return r;
  }

  /**
   * Calcula y asigna las posiciones de los bits de redundancia (potencias de 2)
   * según si el mensaje tiene paridad extendida o no.
   */
  private List<Integer> calculatePositionRedundancyBits() {
    List<Integer> positions = new java.util.ArrayList<>();
    int positionTemp = 0;

    while (Math.pow(2, positionTemp) <= this.maxPosition) {
      positions.add((int) Math.pow(2, positionTemp));
      positionTemp++;
    }

    return positions;
  }

  /**
   * Construye el mapeo de los bits recibidos, etiquetando cada uno según su rol:
   * - "alg": algoritmo
   * - "conf": configuración de longitud de datos (ej. 5 bits)
   * - "r": bit de redundancia Hamming
   * - "d": bit de dato
   * - "rg": bit de paridad global (si aplica)
   */
  public void bitSetupReceived() {
    this.detailLines = new java.util.ArrayList<>();
    this.msgBitsReceived = new java.util.ArrayList<>();
    this.detailLines.add("Construcción del mensaje recibido:");

    for (int i = 1; i <= this.totalBits; i++) {
      int bit = Character.getNumericValue(this.message.charAt(i - 1));
      String tipo;

      if (i == 1) {
        tipo = this.typeBit.get(3); // "alg"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (tipo de algoritmo, alg)", i, bit));
      } else if (i <= 1 + this.bitsConfiguration) {
        tipo = this.typeBit.get(4); // "conf"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (configuración de longitud, conf)", i, bit));
      } else if (this.isExtended && i == this.totalBits) {
        tipo = this.typeBit.get(2); // "rg"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (paridad global, rg)", i, bit));
      } else {
        int logicalIndex = i - (1 + this.bitsConfiguration);

        if (this.posRedundancyBits.contains(logicalIndex + 1)) {
          tipo = this.typeBit.get(1); // "r"
          this.detailLines.add(String.format("\t- Posición %d: bit '%d' (paridad Hamming, r)", i, bit));
        } else {
          tipo = this.typeBit.get(0); // "d"
          this.detailLines.add(String.format("\t- Posición %d: bit '%d' (dato, d)", i, bit));
        }
      }

      this.msgBitsReceived.add(new java.util.AbstractMap.SimpleEntry<>(bit, tipo));
    }

    this.detailLines.add("");
  }

  /**
   * Calcula los bits que están cubiertos por cada bit de redundancia Hamming.
   *
   * Para cada posición de bit de redundancia (por ejemplo: 1, 2, 4, 8...),
   * se determina qué posiciones de la trama de bits son verificadas por ese bit.
   */
  public void assignRedundancyCoverage() {
    this.redundancyCoverageMap = new java.util.HashMap<>();
    this.detailLines.add("Asignación de cobertura de bits de redundancia:");

    // Offset: cantidad de bits antes del bloque codificado (alg + conf)
    int offset = 1 + this.bitsConfiguration;

    for (int r : this.posRedundancyBits) {
      List<Integer> posiciones = new java.util.ArrayList<>();

      for (int i = 1; i <= this.totalBits - offset; i++) {
        int realPos = i + offset;
        boolean isCovered = (i & r) != 0;
        boolean notGlobalBit = !this.isExtended || realPos != this.totalBits;

        if (isCovered && notGlobalBit) {
          posiciones.add(realPos);
        }
      }

      this.redundancyCoverageMap.put(r, posiciones);
      this.detailLines.add(String.format(" - r%d (posición %d) cubre: %s", r, r + offset, posiciones));
    }

    this.detailLines.add("");
  }

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
   * Obtener la cantidad de bits apartados para representar la cantidad de bits de
   * datos
   */
  public int getBitsConfiguration() {
    return this.bitsConfiguration;
  }

  public void setBitsConfiguration(int bitsConfiguration) {
    this.bitsConfiguration = bitsConfiguration;
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
   * Obtener la posición maxima de la cadena de bits (en caso de usar un bit de
   * paridad global)
   */
  public int getMaxPosition() {
    return this.maxPosition;
  }

  public void setMaxPosition(int maxPosition) {
    this.maxPosition = maxPosition;
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