public class ProtocolConfig {

  private String parity;
  private boolean extended;
  private int bits_per_char;
  private Network network;

  // Getters y Setters
  public String getParity() {
    return parity;
  }

  public void setParity(String parity) {
    this.parity = parity;
  }

  public boolean isExtended() {
    return extended;
  }

  public void setExtended(boolean extended) {
    this.extended = extended;
  }

  public int getBits_per_char() {
    return bits_per_char;
  }

  public void setBits_per_char(int bits_per_char) {
    this.bits_per_char = bits_per_char;
  }

  public Network getNetwork() {
    return network;
  }

  public void setNetwork(Network network) {
    this.network = network;
  }

  // Clase interna para la sección "network"
  public static class Network {
    private String host;
    private int port;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }
  }

}
