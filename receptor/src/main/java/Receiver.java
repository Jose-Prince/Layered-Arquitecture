import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Receiver {

  // Entradas
  private String msg;
  private int totalBits;
  private int dataBits;
  private String outputPath;
  private String detailPath;
  private String protocolPath;

  // Documentación y constantes
  private List<String> detailLines = new ArrayList<>();
  private List<String> typeBit = Arrays.asList("d", "r", "rg"); // d: data, r: hamming redundancy, rg: global redundancy

  // Calculo de variables de entrada
  private int redundancyBits;
  private List<Integer> posRedundancyBits = new ArrayList<>();
  private Map<Integer, List<Integer>> redundancyCoverageMap = new HashMap<>();
  private List<Map.Entry<Integer, String>> msgBitsRecived = new ArrayList<>();
  private List<Map.Entry<Integer, String>> msgBitsCalculated = new ArrayList<>();

  // Variables de protocolo
  private boolean isExtended;
  private boolean isEvenParity;
  private ValidationResult resultValidation;
  private Integer extendedParityBit = null;

  // Salida
  private String msgOutput;

  /*
   * Constructor de la clase Receiver
   */
  public Receiver(String msg, int totalBits, int dataBits, String outputPath, String detailPath, String protocolPath) {
    this.msg = msg;
    this.totalBits = totalBits;
    this.dataBits = dataBits;
    this.outputPath = outputPath;
    this.detailPath = detailPath;
    this.protocolPath = protocolPath;

    loadConfig(protocolPath);
    getRedundancyBits();
    getPositionRedundancyBits();
    bitSetupRecived();
    assignRedundancyCoverage();
    setMsgBits();
    setAllRedundancyBits();
    this.resultValidation = validateErrors();
    this.msgOutput = getMsgOutput();
  }

  /*
   * Método para procesar archivo .yaml
   */
  private void loadConfig(String protocolPath) {
    Yaml yaml = new Yaml();
    try (InputStream inputStream = new FileInputStream(protocolPath)) {
      Map<String, Object> config = yaml.load(inputStream);
      this.isExtended = (boolean) config.getOrDefault("extended", true);
      String parity = (String) config.getOrDefault("parity", "even");
      this.isEvenParity = parity.equals("even");
    } catch (IOException e) {
      e.printStackTrace();
      this.isExtended = true;
      this.isEvenParity = true;
    }
  }

  /*
   * Método para saber cuantos bits de redundancia vamos a tener
   */
  public void getRedundancyBits() {
    redundancyBits = 0;
    while (Math.pow(2, redundancyBits) < (dataBits + redundancyBits + 1)) {
      redundancyBits++;
    }
  }

  /*
   * Método para saber en que posiciones están esos bits de redundancia
   */
  public void getPositionRedundancyBits() {
    int positionTemp = 0;
    int maxPosition = isExtended ? totalBits - 1 : totalBits;

    while (Math.pow(2, positionTemp) <= maxPosition) {
      posRedundancyBits.add((int) Math.pow(2, positionTemp));
      positionTemp++;
    }
  }

  /*
   * Método para poder construir el mensaje recibido y tener referencia de los
   * bits de datos, de redundancia y de paridad global
   */
  public void bitSetupRecived() {
    detailLines.add("Construcción del mensaje recibido:");

    for (int i = 1; i <= totalBits; i++) {
      int bit = Character.getNumericValue(msg.charAt(i - 1));
      String tipo;

      if (isExtended && i == totalBits) {
        tipo = typeBit.get(2); // rg
        detailLines.add("\t- Posición " + i + ": bit recibido '" + bit + "' (paridad global, rg)");
      } else if (posRedundancyBits.contains(i)) {
        tipo = typeBit.get(1); // r
        detailLines.add("\t- Posición " + i + ": bit recibido '" + bit + "' (paridad Hamming, r)");
      } else {
        tipo = typeBit.get(0); // d
        detailLines.add("\t- Posición " + i + ": bit recibido '" + bit + "' (dato, d)");
      }

      msgBitsRecived.add(new AbstractMap.SimpleEntry<>(bit, tipo));
    }

    detailLines.add("");

  }

  /**
   * Calcula los bits que están cubiertos por cada bit de redundancia Hamming.
   *
   * Para cada posición de bit de redundancia (por ejemplo: 1, 2, 4, 8...),
   * se determina qué posiciones de la trama de bits son verificadas por ese bit
   * de paridad.
   */
  public void assignRedundancyCoverage() {
    for (int r : posRedundancyBits) {
      List<Integer> posiciones = new ArrayList<>();
      for (int i = 1; i <= totalBits; i++) {
        boolean isCovered = (i & r) != 0;
        boolean notGlobalBit = !isExtended || i != totalBits;

        if (isCovered && notGlobalBit) {
          posiciones.add(i);
        }
      }
      redundancyCoverageMap.put(r, posiciones);
    }
  }

  /*
   * Método para construir msgBitsCalculated a partir de msgBitsRecived.
   * Los bits de tipo "d" se copian igual,
   * los de tipo "r" y "rg" se colocan con valor -1.
   */
  public void setMsgBits() {
    msgBitsCalculated.clear();
    detailLines.add("Inicializando msgBitsCalculated:");
    for (Map.Entry<Integer, String> entry : msgBitsRecived) {
      int bit = entry.getKey();
      String tipo = entry.getValue();

      if ("d".equals(tipo)) {
        msgBitsCalculated.add(new AbstractMap.SimpleEntry<>(bit, tipo));
        detailLines
            .add(String.format("\t- Bit de dato en posición %d con valor %d copiado", msgBitsCalculated.size(), bit));
      } else if ("r".equals(tipo) || "rg".equals(tipo)) {
        msgBitsCalculated.add(new AbstractMap.SimpleEntry<>(-1, tipo));
        detailLines.add(String.format("\t- Bit de redundancia tipo '%s' inicializado en posición %d con valor -1", tipo,
            msgBitsCalculated.size()));
      }
    }
  }

  /*
   * Método que calcula la paridad para un bit de redundancia específico.
   * 
   */
  public int calculateParity(int position) {
    List<Integer> positionsToCheck = redundancyCoverageMap.get(position);
    int count = 0;
    List<String> bitValues = new ArrayList<>();

    for (int pos : positionsToCheck) {
      Integer bit = msgBitsCalculated.get(pos - 1).getKey();
      if (bit != null && bit == 1) {
        count++;
      }
      bitValues.add(pos + "=" + (bit != null ? bit.toString() : "None"));
    }

    int parity = isEvenParity ? count % 2 : (count + 1) % 2;

    detailLines.add(String.format(
        "r%d cubre posiciones %s -> valores: [%s]. Total de 1s: %d. Paridad %s usada -> bit de paridad: %d",
        position, positionsToCheck, String.join(", ", bitValues), count,
        isEvenParity ? "par" : "impar", parity));

    return parity;
  }

  /*
   * Método que calcula la paridad extendida.
   */
  public int calculateParityExtend() {
    List<Map.Entry<Integer, String>> bitsInPositions = msgBitsCalculated.subList(0, totalBits - 1);

    int countOnes = 0;
    StringBuilder bitValuesStr = new StringBuilder();

    for (int i = 0; i < bitsInPositions.size(); i++) {
      Map.Entry<Integer, String> entry = bitsInPositions.get(i);
      Integer bit = entry.getKey();
      int pos = i + 1;

      if (bit != null && bit == 1) {
        countOnes++;
      }

      bitValuesStr.append(pos).append("=").append(bit).append(", ");
    }

    // Eliminar la última coma y espacio
    if (bitValuesStr.length() > 2) {
      bitValuesStr.setLength(bitValuesStr.length() - 2);
    }

    int parity = isEvenParity ? countOnes % 2 : (countOnes + 1) % 2;

    detailLines.add(String.format(
        "Paridad extendida cubre todas las posiciones excepto la final -> valores: [%s]. Total de 1s: %d. Paridad %s usada -> bit global: %d",
        bitValuesStr, countOnes, isEvenParity ? "par" : "impar", parity));

    return parity;
  }

  /*
   * Calcula y asigna todos los bits de redundancia (r) y, si aplica, el bit
   * global (rg).
   */
  public void setAllRedundancyBits() {
    detailLines.add("\nCalculando y asignando bits de redundancia:\n");

    // 1. Calcular paridades Hamming
    for (int position : posRedundancyBits) {
      int parity = calculateParity(position);
      msgBitsCalculated.set(position - 1, new AbstractMap.SimpleEntry<>(parity, typeBit.get(1))); // "r"
      detailLines.add(String.format("\t - Bit de redundancia r%d asignado con valor %d", position, parity));
    }

    // 2. Calcular paridad extendida si está activada
    if (isExtended) {
      int parityExtend = calculateParityExtend();
      msgBitsCalculated.set(totalBits - 1, new AbstractMap.SimpleEntry<>(parityExtend, typeBit.get(2))); // "rg"
      extendedParityBit = parityExtend; // Guardar el valor en el atributo
      detailLines.add(String.format("\t - Bit de redundancia extendida rg asignado con valor %d", parityExtend));
    } else {
      extendedParityBit = null;
    }
  }

  /*
   * Método para determinar si la trama de bits tiene error, si es un único error
   * y la posición que hubo para un error.
   */
  public ValidationResult validateErrors() {
    boolean isError = false;
    boolean isOneError = false;
    int positionError = 0;

    detailLines.add("\n===== DETECCIÓN DE ERRORES =====");
    detailLines.add("Comparación de bits de paridad recibidos vs calculados:");

    detailLines.add("Bits de paridad (posRedundancyBits): " + posRedundancyBits.toString());

    // Comparar bits de paridad recibidos vs recalculados
    for (int parityPos : posRedundancyBits) {
      int received = msgBitsRecived.get(parityPos - 1).getKey();
      int calculated = msgBitsCalculated.get(parityPos - 1).getKey();

      detailLines.add(String.format(" - Posición %d: recibido = %d, calculado = %d", parityPos, received, calculated));

      if (received != calculated) {
        isError = true;
        positionError += parityPos;
        detailLines.add("   -> Diferencia detectada en bit de paridad r" + parityPos);
      }
    }

    if (!isError) {
      detailLines.add("-> No se detectaron errores de paridad.");
    } else {
      detailLines.add("-> Se detectaron errores de paridad.");
      detailLines.add(" - Posición decimal del error estimado: " + positionError);

      if (isExtended) {
        detailLines.add("\nVerificando bit de paridad extendida:");

        int receivedGlobal = msgBitsRecived.get(totalBits - 1).getKey();
        int calculatedGlobal = extendedParityBit != null ? extendedParityBit : -1;

        detailLines.add(" - Paridad extendida recibida: " + receivedGlobal);
        detailLines.add(" - Paridad extendida calculada: " + calculatedGlobal);

        if (receivedGlobal == calculatedGlobal) {
          isOneError = true;
          detailLines.add("-> Bit global coincide -> Se asume un único error, se puede corregir.");
        } else {
          detailLines.add("-> No coincide bit global -> Múltiples errores, no se puede corregir.");
        }
      } else {
        detailLines.add("-> No hay bit de paridad extendida -> No se puede confirmar si hay múltiples errores.");
      }
    }

    detailLines.add("===== FIN DE DETECCIÓN DE ERRORES =====");
    return new ValidationResult(isError, positionError - 1, isOneError);
  }

  /*
   * Método para exportar el reporte a un archivo de texto.
   */
  public void exportToTxt(String filename) {
    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
      writer.println("===== HAMMING TRANSMITTER REPORT =====\n");

      // --- Configuración general ---
      writer.println("Configuración:");
      writer.printf("- Protocolo de data: (%d,%d)%n", totalBits, dataBits);
      writer.printf("- Paridad usada: %s%n", isEvenParity ? "par" : "impar");
      writer.printf("- Paridad extendida: %s%n", isExtended ? "sí" : "no");
      writer.printf("- Bits de datos: %d%n", dataBits);
      writer.printf("- Bits de redundancia Hamming: %d%n", redundancyBits);
      if (isExtended) {
        writer.printf("- Bit de redundancia global (extendido): %d%n",
            extendedParityBit != null ? extendedParityBit : -1);
      }
      writer.printf("- Bits totales (recibidos): %d%n", totalBits);
      writer.printf("- Posiciones de redundancia Hamming: %s%n", posRedundancyBits);
      if (isExtended) {
        writer.printf("- Posición de bit de redundancia global (extendido): %d%n", totalBits);
      }

      writer.println();

      // --- Información RECIBIDA ---
      writer.println("=== Información RECIBIDA ===");
      writer.println("Mensaje recibido (bits de datos):");
      writer.println(msg + "\n");

      writer.println("Bits recibidos (posiciones y tipos):");
      for (Map.Entry<Integer, String> entry : msgBitsRecived) {
        writer.printf("Posición %d: Tipo = %s%n", entry.getKey(), entry.getValue());
      }

      writer.println();

      // --- Información CALCULADA ---
      writer.println("=== Información CALCULADA ===");
      writer.println("Mensaje codificado (msgBitsCalculated):");
      writer.println("Posición\tBit\tTipo");
      for (int i = 0; i < msgBitsCalculated.size(); i++) {
        Map.Entry<Integer, String> entry = msgBitsCalculated.get(i);
        Integer bit = entry.getKey();
        String tipo = entry.getValue();
        writer.printf("%d\t\t%s\t%s%n", i + 1, bit != null ? bit.toString() : "?", tipo);
      }

      writer.println();

      writer.println("Cobertura de cada bit de paridad Hamming:");
      for (Map.Entry<Integer, List<Integer>> entry : redundancyCoverageMap.entrySet()) {
        writer.printf("r%d (pos %d) -> cubre: %s%n", entry.getKey(), entry.getKey(), entry.getValue());
      }

      writer.println();

      writer.println("Mensaje binario final (para transmisión):");
      writer.println(msgOutput);

      writer.println("\n===== FIN DEL REPORTE =====");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Método para exportar el detalle de la codificación a un archivo de texto.
   */
  public void exportDetailTxt(String detailPath) {
    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(detailPath), "UTF-8"))) {
      writer.println("===== DETALLE DE LA CODIFICACIÓN HAMMING =====\n");
      for (String line : detailLines) {
        writer.println(line);
      }
      writer.println("\n===== FIN DEL DETALLE =====");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Método para obtener el mensaje final decodificado.
   */
  public String getMsgOutput() {
    StringBuilder output = new StringBuilder();

    detailLines.add("\n===== PROCESO DE CONSTRUCCIÓN DEL MENSAJE FINAL =====\n");

    if (resultValidation.isError) {
      detailLines.add("-> Se detectó un error en el mensaje.");

      if (resultValidation.isOneError) {
        detailLines.add("-> Es un único error. Se intentará corregir.");

        int pos = resultValidation.positionError;

        if (pos >= 0 && pos < msgBitsCalculated.size()) {
          Integer bitBefore = msgBitsCalculated.get(pos).getKey();
          int bitAfter = (bitBefore != null && bitBefore == 1) ? 0 : 1;

          detailLines.add(" - Posición del error: " + (pos + 1));
          detailLines.add(" - Bit antes de la corrección: " + bitBefore);
          detailLines.add(" - Bit después de la corrección: " + bitAfter);

          msgBitsCalculated.set(pos, new AbstractMap.SimpleEntry<>(bitAfter, msgBitsCalculated.get(pos).getValue()));
        }
      } else {
        detailLines.add("-> No se puede corregir el error. Se requiere retransmisión.");
        detailLines.add(" - Resultado final: RETRANSMITIR");
        detailLines.add("===== FIN DEL PROCESO DE CONSTRUCCIÓN =====");
        return "RETRANSMITIR";
      }
    } else {
      detailLines.add("-> No se detectaron errores. El mensaje es válido.");
    }

    // Obtener solo los bits de datos ("d")
    for (Map.Entry<Integer, String> entry : msgBitsCalculated) {
      if ("d".equals(entry.getValue())) {
        Integer bit = entry.getKey();
        output.append(bit != null ? bit.toString() : "X");
      }
    }

    detailLines.add(" - Mensaje final (solo bits de datos): " + output.toString());
    detailLines.add("===== FIN DEL PROCESO DE CONSTRUCCIÓN =====");

    return output.toString();
  }

}
