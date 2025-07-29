package core;

public class ProtocolMessage {
  private int algorithm;
  private String payload;

  public ProtocolMessage(String rawMessage) {
    this.algorithm = Integer.parseInt(rawMessage.substring(0, 1));
    this.payload = rawMessage.substring(1); // este define que algoritmo es
  }

  public int getAlgorithm() {
    return algorithm;
  }

  public String getPayload() {
    return payload;
  }
}
