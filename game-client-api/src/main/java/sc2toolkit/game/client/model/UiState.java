package sc2toolkit.game.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * An SC2 UI state.
 */
public class UiState {

  private final Collection<UiScreen> screens;

  /**
   * Creates a new instance.
   *
   * @param screens The screens.
   */
  public UiState(Collection<UiScreen> screens) {
    this.screens = new LinkedList<>(screens);
  }

  /**
   * Retrieves the screens.
   *
   * @return A collection of screens.
   */
  public Collection<UiScreen> getScreens() {
    return Collections.unmodifiableCollection(screens);
  }

  /**
   * Determines whether the application is in a game or replay.
   *
   * @return {@code true} if the application is in a game or replay,
   *         {@code false} if the application is in the menus.
   */
  public boolean isInGame() {
    return screens.isEmpty();
  }
}
