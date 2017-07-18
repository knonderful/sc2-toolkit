package sc2toolkit.app.toolkit.overwolf;

/**
 * Overwolf app update-game-time message.
 */
public class UpdateGameTimeMessage {

  private final double displayTime;

  /**
   * Creates a new instance.
   * @param displayTime The in-game display time.
   */
  public UpdateGameTimeMessage(double displayTime) {
    this.displayTime = displayTime;
  }

  /**
   * Retrieves the in-game display time.
   * @return The in-game display time.
   */
  public double getDisplayTime() {
    return displayTime;
  }
}
