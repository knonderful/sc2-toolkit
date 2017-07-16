package sc2toolkit.app.toolkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Notifier for application shutdown events.
 */
public class ShutdownNotifier {

  private final Collection<ShutdownListener> listeners = new ArrayList<>(4);

  /**
   * Adds a listener.
   *
   * @param listener The listener.
   */
  public synchronized void addListener(ShutdownListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener.
   *
   * @param listener The listener.
   */
  public synchronized void removeListener(ShutdownListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all listeners of the shutdown event. All listeners will be be
   * removed in the process.
   */
  public synchronized void notifyShutdown() {
    Iterator<ShutdownListener> it = listeners.iterator();
    while (it.hasNext()) {
      it.next().handleShutdown();
      it.remove();
    }
  }
}
