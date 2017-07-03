package sc2toolkit.common;

import java.util.function.Supplier;
import sc2toolkit.common.exception.TkException;

/**
 * Like {@link Supplier}, but can throw a {@link TkException}.
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

  /**
   * Gets a result.
   *
   * @return a result
   * @throws TkException if something went wrong.
   */
  T get() throws TkException;
}
