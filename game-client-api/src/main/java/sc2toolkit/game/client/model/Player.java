package sc2toolkit.game.client.model;

/**
 * An SC2 player.
 */
public class Player {

  private final long id;
  private final String name;
  private final PlayerType type;
  private final Race race;
  private final Result result;

  /**
   * Creates a new instance.
   *
   * @param id     The ID.
   * @param name   The name.
   * @param type   The type.
   * @param race   The race.
   * @param result The result.
   */
  public Player(long id, String name, PlayerType type, Race race, Result result) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.race = race;
    this.result = result;
  }

  /**
   * Retrieves the ID.
   *
   * @return The ID.
   */
  public long getId() {
    return id;
  }

  /**
   * Retrieves the name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the race.
   *
   * @return The race.
   */
  public Race getRace() {
    return race;
  }

  /**
   * Retrieves the result.
   *
   * @return The result.
   */
  public Result getResult() {
    return result;
  }

  /**
   * Retrieves the type.
   *
   * @return The type.
   */
  public PlayerType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Player{" + "id=" + id + ", name=" + name + ", type=" + type + ", race=" + race + ", result=" + result + '}';
  }
}
