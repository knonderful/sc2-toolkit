package sc2toolkit.common.exception;

/**
 * An exception that occurs when a resource could not be loaded.
 */
public class TkResourceException extends TkException {

  /**
   * Creates a new instance.
   */
  public TkResourceException() {
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   */
  public TkResourceException(String message) {
    super(message);
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   * @param cause   The cause.
   */
  public TkResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance.
   *
   * @param cause The cause.
   */
  public TkResourceException(Throwable cause) {
    super(cause);
  }
}
