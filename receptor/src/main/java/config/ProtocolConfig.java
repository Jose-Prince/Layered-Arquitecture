package config;

public class ProtocolConfig {

  private boolean parityEven;
  private boolean extended;
  private int bits_per_char;
  private Network network;
  private Algorithms algorithms;
  private Hamming hamming;

  // Getters y Setters
  public boolean isParityEven() {
    return parityEven;
  }

  public void setParityEven(boolean parityEven) {
    this.parityEven = parityEven;
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

  public Algorithms getAlgorithms() {
    return algorithms;
  }

  public void setAlgorithms(Algorithms algorithms) {
    this.algorithms = algorithms;
  }

  public Hamming getHamming() {
    return hamming;
  }

  public void setHamming(Hamming hamming) {
    this.hamming = hamming;
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

  // Clase interna para la sección "algorithms"
  public static class Algorithms {
    private int hamming;
    private int fletcher;

    public int getHamming() {
      return hamming;
    }

    public void setHamming(int hamming) {
      this.hamming = hamming;
    }

    public int getFletcher() {
      return fletcher;
    }

    public void setFletcher(int fletcher) {
      this.fletcher = fletcher;
    }
  }

  // Clase interna para la sección "hamming"
  public static class Hamming {
    private int bits_configuration;

    public int getBits_configuration() {
      return bits_configuration;
    }

    public void setBits_configuration(int bits_configuration) {
      this.bits_configuration = bits_configuration;
    }
  }

}
