package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class Report {

  private boolean errorDetected;
  private boolean isOneError;
  private boolean errorResolved;
  private BigInteger dataBitsCount;
  private String receivedMessage;
  private String detectedAlgorithm;
  private String decodedBitMessage;
  private String decodedTextMessage;

  public Report() {
    this.errorDetected = false;
    this.isOneError = false;
    this.errorResolved = false;
    this.dataBitsCount = BigInteger.ZERO;
    this.receivedMessage = "";
    this.detectedAlgorithm = "";
    this.decodedBitMessage = "";
    this.decodedTextMessage = "";
  }

  // Getters
  public boolean isErrorDetected() {
    return errorDetected;
  }

  public boolean isOneError() {
    return isOneError;
  }

  public boolean isErrorResolved() {
    return errorResolved;
  }

  public BigInteger getDataBitsCount() {
    return dataBitsCount;
  }

  public String getReceivedMessage() {
    return receivedMessage;
  }

  public String getDetectedAlgorithm() {
    return detectedAlgorithm;
  }

  public String getDecodedBitMessage() {
    return decodedBitMessage;
  }

  public String getDecodedTextMessage() {
    return decodedTextMessage;
  }

  // Setters
  public void setErrorDetected(boolean errorDetected) {
    this.errorDetected = errorDetected;
  }

  public void isOneError(boolean isOneError) {
    this.isOneError = isOneError;
  }

  public void setErrorResolved(boolean errorResolved) {
    this.errorResolved = errorResolved;
  }

  public void setDataBitsCount(BigInteger dataBitsCount) {
    this.dataBitsCount = dataBitsCount;
  }

  public void setReceivedMessage(String receivedMessage) {
    this.receivedMessage = receivedMessage;
  }

  public void setDetectedAlgorithm(String detectedAlgorithm) {
    this.detectedAlgorithm = detectedAlgorithm;
  }

  public void setDecodedBitMessage(String decodedBitMessage) {
    this.decodedBitMessage = decodedBitMessage;
  }

  public void setDecodedTextMessage(String decodedTextMessage) {
    this.decodedTextMessage = decodedTextMessage;

  }

  public void exportToCsv(String fileName) {
    String directoryPath = "../data/";
    File directory = new File(directoryPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    File file = new File(directoryPath + fileName);
    boolean writeHeader = !file.exists() || file.length() == 0;

    try (FileWriter writer = new FileWriter(file, true)) {
      if (writeHeader) {
        writer.write(
            "errorDetected,isOneError,errorResolved,dataBitsCount,receivedMessage,detectedAlgorithm,decodedBitMessage,decodedTextMessage\n");
      }

      writer.write(String.format("%b,%b,%b,%d,%s,%s,%s,%s\n",
          errorDetected,
          isOneError,
          errorResolved,
          dataBitsCount,
          escapeCsv(receivedMessage),
          escapeCsv(detectedAlgorithm),
          escapeCsv(decodedBitMessage),
          escapeCsv(decodedTextMessage)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Sobrecarga con nombre por defecto
  public void exportToCsv() {
    exportToCsv("receiver_report.csv");
  }

  // Método auxiliar para escapar comas y saltos de línea en texto
  private String escapeCsv(String text) {
    if (text == null)
      return "";
    if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
      text = text.replace("\"", "\"\"");
      return "\"" + text + "\"";
    }
    return text;
  }
}