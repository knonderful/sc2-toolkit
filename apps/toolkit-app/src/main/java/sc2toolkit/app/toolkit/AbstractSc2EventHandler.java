package sc2toolkit.app.toolkit;

import java.util.Collection;
import sc2toolkit.game.client.Sc2ApplicationEventHandler;
import sc2toolkit.game.client.model.Player;

/**
 * Abstract {@link Sc2ApplicationEventHandler} that implements all methods so
 * that not every implementation has to implement methods that it doesn't use.
 */
public abstract class AbstractSc2EventHandler implements Sc2ApplicationEventHandler {

  @Override
  public void startSc2() {
  }

  @Override
  public void endSc2() {
  }

  @Override
  public void enterMenus() {
  }

  @Override
  public void enterGame(Collection<Player> players) {
  }

  @Override
  public void enterReplay(Collection<Player> players) {
  }

  @Override
  public void updateDisplayTime(double displayTime) {
  }

  @Override
  public void playerWon(Player player) {
  }

  @Override
  public void playerLost(Player player) {
  }

  @Override
  public void playerTied(Player player) {
  }
}
