package sc2toolkit.game.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * An SC2 UI state.
 */
public class UiState {

  private Collection<String> activeScreens;

  private UiState() {
    // For GSON
  }

  /**
   * Creates a new instance.
   *
   * @param activeScreens The screens.
   */
  public UiState(Collection<String> activeScreens) {
    this.activeScreens = new LinkedList<>(activeScreens);
  }

  /**
   * Retrieves the screens.
   *
   * @return A collection of screens.
   */
  public Collection<String> getScreens() {
    return Collections.unmodifiableCollection(activeScreens);
  }

  /**
   * Determines whether the application is in a game or replay.
   *
   * @return {@code true} if the application is in a game or replay,
   *         {@code false} if the application is in the menus.
   */
  public boolean isInGame() {
    return activeScreens.isEmpty();
  }
}
