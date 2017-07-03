package sc2toolkit.common.exception;

/**
 * An SC2 tool kit exception.
 */
public class TkException extends Exception {

  /**
   * Creates a new instance.
   */
  public TkException() {
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   */
  public TkException(String message) {
    super(message);
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   * @param cause   The cause.
   */
  public TkException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance.
   *
   * @param cause The cause.
   */
  public TkException(Throwable cause) {
    super(cause);
  }
}
