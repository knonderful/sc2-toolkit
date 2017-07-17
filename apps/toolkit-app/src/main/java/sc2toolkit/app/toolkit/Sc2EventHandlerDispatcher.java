package sc2toolkit.app.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import sc2toolkit.game.client.Sc2ApplicationEventHandler;
import sc2toolkit.game.client.model.Player;

/**
 * An {@link Sc2ApplicationEventHandler} that dispatches calls to multiple other
 * handlers.
 */
public class Sc2EventHandlerDispatcher implements Sc2ApplicationEventHandler {

  private static final Logger LOG = Logger.getLogger(Sc2EventHandlerDispatcher.class.getName());
  private final Collection<Sc2ApplicationEventHandler> handlers;

  public Sc2EventHandlerDispatcher(Sc2ApplicationEventHandler... handlers) {
    this.handlers = new ArrayList<>(Arrays.asList(handlers));
  }

  @Override
  public void startSc2() {
    forEachHandler(Sc2ApplicationEventHandler::startSc2);
  }

  @Override
  public void endSc2() {
    forEachHandler(Sc2ApplicationEventHandler::endSc2);
  }

  @Override
  public void enterMenus() {
    forEachHandler(Sc2ApplicationEventHandler::enterMenus);
  }

  @Override
  public void enterGame(Collection<Player> players) {
    forEachHandler(handler -> handler.enterGame(players));
  }

  @Override
  public void enterReplay(Collection<Player> players) {
    forEachHandler(handler -> handler.enterReplay(players));
  }

  @Override
  public void updateDisplayTime(double displayTime) {
    forEachHandler(handler -> handler.updateDisplayTime(displayTime));
  }

  @Override
  public void playerWon(Player player) {
    forEachHandler(handler -> handler.playerWon(player));
  }

  @Override
  public void playerLost(Player player) {
    forEachHandler(handler -> handler.playerLost(player));
  }

  @Override
  public void playerTied(Player player) {
    forEachHandler(handler -> handler.playerTied(player));
  }

  private void forEachHandler(Consumer<Sc2ApplicationEventHandler> handlerConsumer) {
    handlers.forEach(handler -> {
      try {
        handlerConsumer.accept(handler);
      } catch (Exception e) {
        // Don't allow for an exception in a handler to block the next call
        LOG.log(Level.WARNING, "Error in event handler.", e);
      }
    });
  }
}
