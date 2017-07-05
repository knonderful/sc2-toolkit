package sc2toolkit.game.client;

import sc2toolkit.game.client.model.GameState;
import sc2toolkit.game.client.model.UiState;

/**
 * A listener of SC2 application states.
 */
public interface Sc2AppStateListener {

  /**
   * Notifies the listener that the game state has been updated.
   *
   * @param uiOld   The old {@link UiState}.
   * @param uiNew   The new {@link UiState}.
   * @param gameOld The old {@link GameState}.
   * @param gameNew The new {@link GameState}.
   */
  void updateStates(UiState uiOld, UiState uiNew, GameState gameOld, GameState gameNew);
}
