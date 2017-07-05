package sc2toolkit.game.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * The SC2 application game state.
 */
public class GameState {

  private final boolean replay;
  private final double displayTime;
  private final Collection<Player> players;

  /**
   * Creates a new instance.
   *
   * @param replay      Flag that specifies whether this is a replay.
   * @param displayTime The display time.
   * @param players     The players.
   */
  public GameState(boolean replay, double displayTime, Collection<Player> players) {
    this.replay = replay;
    this.displayTime = displayTime;
    this.players = new TreeSet<>((lhs, rhs) -> {
      if (lhs == null) {
        if (rhs == null) {
          return 0;
        }

        return -1;
      }

      if (rhs == null) {
        return 1;
      }

      return Long.compare(lhs.getId(), rhs.getId());
    });
    this.players.addAll(players);
  }

  /**
   * Retrieves the display time.
   *
   * @return The display time in seconds.
   */
  public double getDisplayTime() {
    return displayTime;
  }

  /**
   * Retrieves the players.
   *
   * @return A collection of players.
   */
  public Collection<Player> getPlayers() {
    return Collections.unmodifiableCollection(players);
  }

  /**
   * Determines whether this is a replay or a game.
   *
   * @return {@code true} if it is a replay, otherwise {@code false}.
   */
  public boolean isReplay() {
    return replay;
  }

  @Override
  public String toString() {
    return "GameState{" + "replay=" + replay + ", displayTime=" + displayTime + ", players=" + players + '}';
  }
}
