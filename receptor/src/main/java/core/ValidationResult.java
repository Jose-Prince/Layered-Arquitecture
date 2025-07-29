package core;

public class ValidationResult {
  private boolean isError;
  private int positionError;
  private boolean isOneError;
  private String decodedMessage;

  public ValidationResult(boolean isError, int positionError, boolean isOneError, String decodedMessage) {
    this.isError = isError;
    this.positionError = positionError;
    this.isOneError = isOneError;
    this.decodedMessage = decodedMessage;
  }

  public boolean isError() {
    return isError;
  }

  public void setError(boolean isError) {
    this.isError = isError;
  }

  public int getPositionError() {
    return positionError;
  }

  public void setPositionError(int positionError) {
    this.positionError = positionError;
  }

  public boolean isOneError() {
    return isOneError;
  }

  public void setOneError(boolean isOneError) {
    this.isOneError = isOneError;
  }

  public String getDecodedMessage() {
    return decodedMessage;
  }

  public void setDecodedMessage(String decodedMessage) {
    this.decodedMessage = decodedMessage;
  }
}
