package sc2toolkit.game.client;

import java.util.Collection;
import sc2toolkit.game.client.model.Player;

/**
 * SC2 application event handler.
 */
public interface Sc2ApplicationEventHandler {

  /**
   * Notifies that the SC2 application has been started.
   */
  void startSc2();

  /**
   * Notifies that the SC2 application has been closed.
   */
  void endSc2();

  /**
   * Notifies that the SC2 application is in the menus.
   */
  void enterMenus();

  /**
   * Notifies that the SC2 application has entered a game.
   *
   * @param players All players in the game.
   */
  void enterGame(Collection<Player> players);

  /**
   * Notifies that the SC2 application has entered a replay.
   *
   * @param players All players in the replay.
   */
  void enterReplay(Collection<Player> players);

  /**
   * Notifies the new display time in the game or replay.
   *
   * @param displayTime The time in seconds.
   */
  void updateDisplayTime(double displayTime);

  /**
   * Notifies that a player has won.
   *
   * @param player The player.
   */
  void playerWon(Player player);

  /**
   * Notifies that a player has lost.
   *
   * @param player The player.
   */
  void playerLost(Player player);

  /**
   * Notifies that a player has tied.
   *
   * @param player The player.
   */
  void playerTied(Player player);
}
