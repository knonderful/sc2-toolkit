package sc2toolkit.game.client.model;

import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The SC2 application game state.
 */
public class GameState {

  private boolean isReplay;
  private double displayTime;
  private Collection<Player> players;

  private GameState() {
    // For GSON
  }

  /**
   * Creates a new instance.
   *
   * @param replay      Flag that specifies whether this is a replay.
   * @param displayTime The display time.
   * @param players     The players.
   */
  public GameState(boolean replay, double displayTime, Collection<Player> players) {
    this.isReplay = replay;
    this.displayTime = displayTime;
    this.players = new TreeSet<>(GameState::sortPlayers);
    this.players.addAll(players);
  }

  private static int sortPlayers(Player lhs, Player rhs) {
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
    if (players instanceof TreeSet) {
      return players;
    }

    // Sort if necessary
    return players.stream()
            .sorted(GameState::sortPlayers)
            .collect(Collectors.toList());
  }

  /**
   * Determines whether this is a replay or a game.
   *
   * @return {@code true} if it is a replay, otherwise {@code false}.
   */
  public boolean isReplay() {
    return isReplay;
  }

  @Override
  public String toString() {
    return "GameState{" + "replay=" + isReplay + ", displayTime=" + displayTime + ", players=" + players + '}';
  }
}
