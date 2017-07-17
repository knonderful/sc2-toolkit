package sc2toolkit.app.toolkit.overwolf;

import sc2toolkit.game.client.model.Player;

/**
 * Overwolf app start-game message.
 */
public class StartGameMessage {

  private final Player opponent;

  /**
   * Creates a new instance.
   *
   * @param opponent The opponent.
   */
  public StartGameMessage(Player opponent) {
    this.opponent = opponent;
  }

  /**
   * Retrieves the opponent.
   *
   * @return The opponent.
   */
  public Player getOpponent() {
    return opponent;
  }
}
