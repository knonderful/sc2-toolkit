package sc2toolkit.app.toolkit.overwolf;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import sc2toolkit.app.toolkit.ShutdownListener;
import sc2toolkit.app.toolkit.ShutdownNotifier;

/**
 * A factory for {@link OverwolfAppConnector}s.
 */
public class OverwolfAppConnectorFactory {

  private static final Logger LOG = Logger.getLogger(ShutdownListenerImpl.class.getName());
  private final ShutdownNotifier shutdownNotifier;

  /**
   * Creates a new instance.
   *
   * @param shutdownNotifier The shutdown notifier.
   */
  public OverwolfAppConnectorFactory(ShutdownNotifier shutdownNotifier) {
    this.shutdownNotifier = shutdownNotifier;
  }

  /**
   * Creates a new {@link OverwolfAppConnector}.
   *
   * @param address The remote address of the Overwolf application.
   * @return A {@link CompletionStage} for the {@link OverwolfAppConnector}.
   */
  public CompletionStage<OverwolfAppConnector> create(InetSocketAddress address) {
    HttpClient client = new HttpClient(address);
    ShutdownListenerImpl shutdownListener = new ShutdownListenerImpl(client::stop);
    CompletionStage<OverwolfAppConnector> out = client.start().thenApply(ignore -> {
      shutdownNotifier.addListener(shutdownListener);
      return new OverwolfAppConnectorImpl(client);
    });

    return out;
  }

  private class ShutdownListenerImpl implements ShutdownListener {

    private final Supplier<CompletionStage<Void>> shutdownCallback;

    ShutdownListenerImpl(Supplier<CompletionStage<Void>> shutdownCallback) {
      this.shutdownCallback = shutdownCallback;
    }

    void triggerShutdown() {
      try {
        shutdownCallback.get().whenComplete((ignore, exception) -> {
          if (exception != null) {
            LOG.log(Level.WARNING, String.format("%s could not be shut down gracefully.", HttpClient.class.getSimpleName()), exception);
            return;
          }

          LOG.info(String.format("%s shut down successfully.", HttpClient.class.getSimpleName()));
        }).toCompletableFuture().get(20, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException ex) {
        LOG.log(Level.SEVERE, String.format("Could not shut down %s in time.", HttpClient.class.getSimpleName()), ex);
      }
    }

    @Override
    public void handleShutdown() {
      triggerShutdown();
    }
  }
}
