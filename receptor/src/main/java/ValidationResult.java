public class ValidationResult {
  public boolean isError;
  public int positionError;
  public boolean isOneError;

  public ValidationResult(boolean isError, int positionError, boolean isOneError) {
    this.isError = isError;
    this.positionError = positionError;
    this.isOneError = isOneError;
  }
}
