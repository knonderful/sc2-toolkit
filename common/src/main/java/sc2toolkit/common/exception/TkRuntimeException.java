package sc2toolkit.common.exception;

import sc2toolkit.common.ThrowingSupplier;

/**
 * An SC2 tool kit run-time exception.
 */
public class TkRuntimeException extends RuntimeException {

  /**
   * Creates a new instance.
   */
  public TkRuntimeException() {
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   */
  public TkRuntimeException(String message) {
    super(message);
  }

  /**
   * Creates a new instance.
   *
   * @param message The message.
   * @param cause   The cause.
   */
  public TkRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance.
   *
   * @param cause The cause.
   */
  public TkRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Wraps a {@link ThrowingSupplier} with a {@link TkRuntimeException}.
   *
   * @param <T>      The return type.
   * @param supplier The supplier.
   * @return The return value.
   * @throws TkRuntimeException if the supplier throws a
   *                            {@link TkRuntimeException}.
   */
  public static <T> T wrap(ThrowingSupplier<T> supplier) throws TkRuntimeException {
    try {
      return supplier.get();
    } catch (TkException e) {
      throw new TkRuntimeException(e);
    }
  }
}
