package sc2toolkit.game.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import sc2toolkit.game.client.model.GameState;
import sc2toolkit.game.client.model.Player;
import sc2toolkit.game.client.model.PlayerType;
import sc2toolkit.game.client.model.Race;
import sc2toolkit.game.client.model.Result;
import sc2toolkit.game.client.model.UiScreen;
import sc2toolkit.game.client.model.UiState;

/**
 * The {@link Sc2StateResolver} retrieves the current state of the SC2
 * application.
 */
public class Sc2StateResolver {

  private static final String KEY_ACTIVE_SCREENS = "activeScreens";
  private static final String KEY_DISPLAY_TIME = "displayTime";
  private static final String KEY_IS_REPLAY = "isReplay";
  private static final String KEY_PLAYERS = "players";

  private static InputStream inputStreamTo(String path) throws IOException {
    StringBuilder sb = new StringBuilder("http://localhost:6119/");
    URL url = new URL(sb.append(path).toString());
    return url.openStream();
  }

  /**
   * Retrieves the current UI state.
   *
   * @return The {@link UiState}.
   * @throws IOException If the state could not be retrieved.
   */
  public UiState getUiState() throws IOException {
    JSONObject root;
    try (InputStream inputStream = inputStreamTo("ui")) {
      root = new JSONObject(new JSONTokener(inputStream));
    }

    if (!root.has(KEY_ACTIVE_SCREENS)) {
      return null;
    }

    JSONArray screens = root.getJSONArray(KEY_ACTIVE_SCREENS);
    Collection<UiScreen> uiScreens = new ArrayList<>(screens.length());
    for (int i = 0; i < screens.length(); i++) {
      String path = screens.getString(i);
      uiScreens.add(new UiScreen(path));
    }
    return new UiState(uiScreens);
  }

  /**
   * Retrieves the current game state.
   *
   * @return The {@link GameState}.
   * @throws IOException If the state could not be retrieved.
   */
  public GameState getGameState() throws IOException {
    JSONObject root;
    try (InputStream inputStream = inputStreamTo("game")) {
      root = new JSONObject(new JSONTokener(inputStream));
    }

    if (!root.has(KEY_IS_REPLAY) || !root.has(KEY_DISPLAY_TIME) || !root.has(KEY_PLAYERS)) {
      return null;
    }

    boolean isReplay = root.getBoolean(KEY_IS_REPLAY);
    double displayTime = root.getDouble(KEY_DISPLAY_TIME);
    JSONArray players = root.getJSONArray(KEY_PLAYERS);
    Collection<Player> playersColl = new ArrayList<>(players.length());
    for (int i = 0; i < players.length(); i++) {
      JSONObject player = players.getJSONObject(i);
      playersColl.add(new Player(
              player.getLong("id"),
              player.getString("name"),
              parsePlayerType(player.getString("type")),
              parseRace(player.getString("race")),
              parseResult(player.getString("result"))
      ));
    }

    return new GameState(isReplay, displayTime, playersColl);
  }

  private static PlayerType parsePlayerType(String value) {
    if ("user".equals(value)) {
      return PlayerType.USER;
    }
    if ("computer".equals(value)) {
      return PlayerType.COMPUTER;
    }
    throw new IllegalArgumentException(String.format("Unknown player type: %s.", value));
  }

  private static Race parseRace(String value) {
    if ("random".equals(value)) {
      return Race.RANDOM;
    }
    if ("Terr".equals(value)) {
      return Race.TERRAN;
    }
    if ("Zerg".equals(value)) {
      return Race.ZERG;
    }
    if ("Prot".equals(value)) {
      return Race.PROTOSS;
    }
    throw new IllegalArgumentException(String.format("Unknown race: %s.", value));
  }

  private static Result parseResult(String value) {
    if ("Undecided".equals(value)) {
      return Result.UNDECIDED;
    }
    if ("Victory".equals(value)) {
      return Result.VICTORY;
    }
    if ("Defeat".equals(value)) {
      return Result.DEFEAT;
    }
    if ("Tie".equals(value)) {
      return Result.TIE;
    }
    throw new IllegalArgumentException(String.format("Unknown result: %s.", value));
  }

}
