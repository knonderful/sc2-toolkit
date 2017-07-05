package sc2toolkit.game.client.model;

import static java.util.Objects.requireNonNull;

/**
 * An SC2 UI screen.
 *
 * @author knonderful <knonderful@github.com>
 */
public class UiScreen {

  private final String path;

  /**
   * Creates a new instance.
   *
   * @param path The path.
   */
  public UiScreen(String path) {
    this.path = requireNonNull(path, "Argument path can not be null.");
  }

  /**
   * Retrieves the path.
   *
   * @return The path.
   */
  public String getPath() {
    return path;
  }
}
