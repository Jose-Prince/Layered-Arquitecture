package decoders;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class HammingDecoder {
  // Entradas
  private String message;
  private int bitsConfiguration;
  private boolean isExtended;
  private boolean isEvenParity;
  private String rawMessage;
  private BigInteger dataBits;
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
  private List<String> typeBit = List.of("d", "r", "rg");

  /*
   * Constructor
   */
  public HammingDecoder(String message, int bitsConfiguration, boolean isExtended, boolean isEvenParity) {
    this.message = message;
    this.bitsConfiguration = bitsConfiguration;
    this.isExtended = isExtended;
    this.isEvenParity = isEvenParity;

    // datos calculados
    this.rawMessage = this.message.substring(1 + this.bitsConfiguration);

    // Inicio
    this.detailLines = new java.util.ArrayList<>();
    this.detailLines.add("Raw message usado para HammingDecoder: " + this.rawMessage);

    this.totalBits = rawMessage.length();
    this.dataBits = extractDataBits();
    this.maxPosition = this.isExtended ? this.totalBits - 1 : this.totalBits;
    this.redundancyBits = calculateRedundancyBits();
    this.posRedundancyBits = calculatePositionRedundancyBits();
    bitSetupReceived();
    assignRedundancyCoverage();
    setMsgBitsCalculated();
    setAllRedundancyBits();
    this.validationResult = decodeMsg();
  }

  /**
   * Obtener la cantidad de data bits dado el valor apartado para representar los
   * bits de data
   */
  private BigInteger extractDataBits() {
    String bits = message.substring(1, 1 + this.bitsConfiguration);
    return new BigInteger(bits, 2);
  }

  /*
   * Método para saber cuantos bits de redundancia vamos a tener
   */
  private int calculateRedundancyBits() {
    int r = 0;
    while (BigInteger.valueOf(2).pow(r).compareTo(this.dataBits.add(BigInteger.valueOf(r + 1))) < 0) {
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
    this.msgBitsReceived = new java.util.ArrayList<>();
    this.detailLines.add("Construcción del mensaje recibido:");

    for (int i = 1; i <= this.totalBits; i++) {
      int bit = Character.getNumericValue(this.rawMessage.charAt(i - 1));
      String tipo;

      if (this.isExtended && i == this.totalBits) {
        tipo = this.typeBit.get(2); // "rg"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (paridad global, rg)", i, bit));
      } else if (this.posRedundancyBits.contains(i)) {
        tipo = this.typeBit.get(1); // "r"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (paridad Hamming, r)", i, bit));
      } else {
        tipo = this.typeBit.get(0); // "d"
        this.detailLines.add(String.format("\t- Posición %d: bit '%d' (dato, d)", i, bit));
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

    for (int r : this.posRedundancyBits) {
      List<Integer> posiciones = new java.util.ArrayList<>();

      for (int i = 1; i <= this.totalBits; i++) {
        boolean isCovered = (i & r) != 0;
        boolean notGlobalBit = !this.isExtended || i != this.totalBits;

        if (isCovered && notGlobalBit) {
          posiciones.add(i);
        }
      }

      this.redundancyCoverageMap.put(r, posiciones);
      this.detailLines.add(String.format(" - r%d (posición %d) cubre: %s", r, r, posiciones));
    }

    this.detailLines.add("");
  }

  /**
   * Construye `msgBitsCalculated` a partir de `msgBitsReceived`.
   * - Copia los bits de datos ("d") tal cual.
   * - Inicializa los bits de redundancia ("r", "rg") con -1 para ser
   * recalculados.
   */
  public void setMsgBitsCalculated() {
    this.msgBitsCalculated = new java.util.ArrayList<>();
    this.detailLines.add("Inicializando msgBitsCalculated:");

    for (Map.Entry<Integer, String> entry : this.msgBitsReceived) {
      int bit = entry.getKey();
      String tipo = entry.getValue();

      if ("d".equals(tipo)) {
        this.msgBitsCalculated.add(new java.util.AbstractMap.SimpleEntry<>(bit, tipo));
        this.detailLines.add(
            String.format("\t- Bit de dato en posición %d con valor %d copiado", this.msgBitsCalculated.size(), bit));
      } else if ("r".equals(tipo) || "rg".equals(tipo)) {
        this.msgBitsCalculated.add(new java.util.AbstractMap.SimpleEntry<>(-1, tipo));
        this.detailLines.add(String.format("\t- Bit de redundancia tipo '%s' inicializado en posición %d con valor -1",
            tipo, this.msgBitsCalculated.size()));
      }
    }

    this.detailLines.add("");
  }

  /**
   * Calcula la paridad para un bit de redundancia específico.
   * 
   * - Usa los bits de `msgBitsCalculated` (que ya tiene datos y -1 en
   * redundancias).
   * - Usa `redundancyCoverageMap` para saber qué posiciones afecta el bit de
   * paridad.
   * - Genera el detalle del proceso en `detailLines`.
   */
  public int calculateParity(int position) {
    List<Integer> positionsToCheck = this.redundancyCoverageMap.get(position);
    int count = 0;
    List<String> bitValues = new java.util.ArrayList<>();

    for (int pos : positionsToCheck) {
      int bit = this.msgBitsCalculated.get(pos - 1).getKey();
      if (bit == 1) {
        count++;
      }
      bitValues.add(pos + "=" + bit);
    }

    int parity = this.isEvenParity ? count % 2 : (count + 1) % 2;

    this.detailLines.add(String.format(
        "r%d cubre posiciones %s -> valores: [%s]. Total de 1s: %d. Paridad %s usada -> bit de paridad: %d",
        position,
        positionsToCheck,
        String.join(", ", bitValues),
        count,
        this.isEvenParity ? "par" : "impar",
        parity));

    return parity;
  }

  /**
   * Calcula la paridad extendida del mensaje.
   * 
   * Recorre todos los bits de `msgBitsCalculated` excepto el último (que
   * corresponde al bit global),
   * y calcula la paridad según el tipo configurado.
   */
  public int calculateParityExtend() {
    List<Map.Entry<Integer, String>> bitsInPositions = this.msgBitsCalculated.subList(0, this.totalBits - 1);

    int countOnes = 0;
    StringBuilder bitValuesStr = new StringBuilder();

    for (int i = 0; i < bitsInPositions.size(); i++) {
      Map.Entry<Integer, String> entry = bitsInPositions.get(i);
      int bit = entry.getKey();
      int pos = i + 1;

      if (bit == 1) {
        countOnes++;
      }

      bitValuesStr.append(pos).append("=").append(bit).append(", ");
    }

    // Eliminar la última coma y espacio
    if (bitValuesStr.length() > 0) {
      bitValuesStr.setLength(bitValuesStr.length() - 2);
    }

    int parity = this.isEvenParity ? countOnes % 2 : (countOnes + 1) % 2;

    this.detailLines.add(String.format(
        "Paridad extendida cubre todas las posiciones excepto la final -> valores: [%s]. Total de 1s: %d. Paridad %s usada -> bit global: %d",
        bitValuesStr.toString(), countOnes, this.isEvenParity ? "par" : "impar", parity));

    return parity;
  }

  /**
   * Calcula y asigna todos los bits de redundancia (r) y, si aplica, el bit de
   * paridad global (rg).
   */
  public void setAllRedundancyBits() {
    this.detailLines.add("Calculando y asignando bits de redundancia:");

    // 1. Calcular paridades Hamming
    for (int position : this.posRedundancyBits) {
      int parity = this.calculateParity(position);
      this.msgBitsCalculated.set(position - 1, new java.util.AbstractMap.SimpleEntry<>(parity, this.typeBit.get(1))); // "r"
      this.detailLines.add(String.format("\t- Bit de redundancia r%d asignado con valor %d", position, parity));
    }

    // 2. Calcular paridad extendida si está activada
    if (this.isExtended) {
      int parityExtend = this.calculateParityExtend();
      this.msgBitsCalculated.set(this.totalBits - 1,
          new java.util.AbstractMap.SimpleEntry<>(parityExtend, this.typeBit.get(2))); // "rg"
      this.extendedParityBit = parityExtend;
      this.detailLines.add(String.format("\t- Bit de redundancia extendida rg asignado con valor %d", parityExtend));
    } else {
      this.extendedParityBit = null;
    }

    this.detailLines.add("");
  }

  /**
   * Verifica si existen errores en los bits de paridad y estima la posición del
   * error.
   * Devuelve un objeto ValidationResult con el estado del análisis.
   */
  public core.ValidationResult validateErrors() {
    boolean isError = false;
    boolean isOneError = false;
    int positionError = 0;

    this.detailLines.add("===== DETECCIÓN DE ERRORES =====");
    this.detailLines.add("Comparación de bits de paridad recibidos vs calculados:");
    this.detailLines.add("Bits de paridad (posRedundancyBits): " + this.posRedundancyBits);

    // Comparar cada bit de paridad recibido vs calculado
    for (int parityPos : this.posRedundancyBits) {
      int received = this.msgBitsReceived.get(parityPos - 1).getKey();
      int calculated = this.msgBitsCalculated.get(parityPos - 1).getKey();

      this.detailLines
          .add(String.format(" - Posición %d: recibido = %d, calculado = %d", parityPos, received, calculated));

      if (received != calculated) {
        isError = true;
        positionError += parityPos;
        this.detailLines.add("   -> Diferencia detectada en bit de paridad r" + parityPos);
      }
    }

    if (!isError) {
      this.detailLines.add("-> No se detectaron errores de paridad.");
    } else {
      this.detailLines.add("-> Se detectaron errores de paridad.");
      this.detailLines.add(" - Posición decimal del error estimado: " + positionError);

      if (this.isExtended) {
        this.detailLines.add("\nVerificando bit de paridad extendida:");

        int receivedGlobal = this.msgBitsReceived.get(this.totalBits - 1).getKey();
        int calculatedGlobal = this.extendedParityBit != null ? this.extendedParityBit : -1;

        this.detailLines.add(" - Paridad extendida recibida: " + receivedGlobal);
        this.detailLines.add(" - Paridad extendida calculada: " + calculatedGlobal);

        if (receivedGlobal == calculatedGlobal) {
          isOneError = true;
          this.detailLines.add("-> Bit global coincide -> Se asume un único error, se puede corregir.");
        } else {
          this.detailLines.add("-> No coincide bit global -> Múltiples errores, no se puede corregir.");
        }
      } else {
        this.detailLines.add("-> No hay bit de paridad extendida -> No se puede confirmar si hay múltiples errores.");
      }
    }

    this.detailLines.add("===== FIN DE DETECCIÓN DE ERRORES =====");

    this.validationResult = new core.ValidationResult(
        isError,
        positionError - 1, // Indexación base 0
        isOneError,
        null // Mensaje decodificado se asignará luego
    );

    return this.validationResult;
  }

  /**
   * Método que evalúa errores, intenta corregir (si aplica) y reconstruye el
   * mensaje.
   * Se actualiza y retorna el ValidationResult.
   */
  public core.ValidationResult decodeMsg() {
    // Paso 1: Validar errores y obtener el resultado base
    core.ValidationResult result = this.validateErrors();

    this.detailLines.add("\n===== PROCESO DE CONSTRUCCIÓN DEL MENSAJE FINAL =====");

    // Paso 2: Manejo de errores
    if (result.isError()) {
      this.detailLines.add("-> Se detectó un error en el mensaje.");

      if (result.isOneError()) {
        this.detailLines.add("-> Es un único error. Se intentará corregir.");

        int pos = result.getPositionError();

        if (pos >= 0 && pos < this.msgBitsCalculated.size()) {
          Integer bitBefore = this.msgBitsCalculated.get(pos).getKey();
          int bitAfter = (bitBefore != null && bitBefore == 1) ? 0 : 1;

          this.detailLines.add(" - Posición del error: " + (pos + 1));
          this.detailLines.add(" - Bit antes de la corrección: " + bitBefore);
          this.detailLines.add(" - Bit después de la corrección: " + bitAfter);

          this.msgBitsCalculated.set(pos,
              new java.util.AbstractMap.SimpleEntry<>(bitAfter, this.msgBitsCalculated.get(pos).getValue()));
        }

      } else {
        this.detailLines.add("-> No se puede corregir el error. Se requiere retransmisión.");
        this.detailLines.add(" - Resultado final: RETRANSMITIR");
        this.detailLines.add("===== FIN DEL PROCESO DE CONSTRUCCIÓN =====");

        result.setDecodedMessage(null);
        return result;
      }

    } else {
      this.detailLines.add("-> No se detectaron errores. El mensaje es válido.");
    }

    // Paso 3: Extraer los bits de datos
    StringBuilder output = new StringBuilder();

    for (Map.Entry<Integer, String> entry : this.msgBitsCalculated) {
      if ("d".equals(entry.getValue())) {
        Integer bit = entry.getKey();
        output.append(bit != null ? bit.toString() : "X");
      }
    }

    this.detailLines.add(" - Mensaje final (solo bits de datos): " + output);
    this.detailLines.add("===== FIN DEL PROCESO DE CONSTRUCCIÓN =====");

    result.setDecodedMessage(output.toString());
    return result;
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
   * Mensaje sin el encabezado (bits de algoritmo y de información configuración)
   */
  public String getRawMessage() {
    return this.rawMessage;
  }

  public void setRawMessage(String rawMessage) {
    this.rawMessage = rawMessage;
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
  public BigInteger getDataBits() {
    return this.dataBits;
  }

  public void setDataBits(BigInteger dataBits) {
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