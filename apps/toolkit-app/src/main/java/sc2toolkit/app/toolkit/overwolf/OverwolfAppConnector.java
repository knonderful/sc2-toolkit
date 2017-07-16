package sc2toolkit.app.toolkit.overwolf;

import java.util.concurrent.CompletionStage;
import sc2toolkit.game.client.model.Player;

/**
 * The interface to the SC2 tool kit Overwolf application.
 */
public interface OverwolfAppConnector {

  /**
   * Sends game start information.
   *
   * @param displayTime The display time.
   * @param opponent    The opponent.
   * @return A {@link CompletionStage} for the operation result.
   */
  CompletionStage<Void> startGame(float displayTime, Player opponent); // + BuildOrder

  /**
   * Sends game time information.
   *
   * @param displayTime The display time.
   * @return A {@link CompletionStage} for the operation result.
   */
  CompletionStage<Void> updateGameTime(float displayTime);

  /**
   * Sends game end information.
   *
   * @return A {@link CompletionStage} for the operation result.
   */
  CompletionStage<Void> endGame();
}
