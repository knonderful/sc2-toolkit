package sc2toolkit.game.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import sc2toolkit.game.client.model.GameState;
import sc2toolkit.game.client.model.PlayerType;
import sc2toolkit.game.client.model.Race;
import sc2toolkit.game.client.model.Result;
import sc2toolkit.game.client.model.UiState;

/**
 * The {@link Sc2StateResolver} retrieves the current state of the SC2
 * application.
 */
public class Sc2StateResolver {

  private final Gson gson;

  public Sc2StateResolver() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(PlayerType.class, new PlayerTypeDeserializer());
    builder.registerTypeAdapter(Race.class, new RaceDeserializer());
    builder.registerTypeAdapter(Result.class, new ResultDeserializer());
    this.gson = builder.create();
  }

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
    try (InputStream inputStream = inputStreamTo("ui")) {
      return gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), UiState.class);
    }
  }

  /**
   * Retrieves the current game state.
   *
   * @return The {@link GameState}.
   * @throws IOException If the state could not be retrieved.
   */
  public GameState getGameState() throws IOException {
    try (InputStream inputStream = inputStreamTo("game")) {
      return gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), GameState.class);
    }
  }

  private static class PlayerTypeDeserializer implements JsonDeserializer<PlayerType> {

    @Override
    public PlayerType deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
      String value = je.getAsString();
      if ("user".equals(value)) {
        return PlayerType.USER;
      }
      if ("computer".equals(value)) {
        return PlayerType.COMPUTER;
      }
      throw new IllegalArgumentException(String.format("Unknown player type: %s.", value));
    }
  }

  private static class RaceDeserializer implements JsonDeserializer<Race> {

    @Override
    public Race deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
      String value = je.getAsString();
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
  }

  private static class ResultDeserializer implements JsonDeserializer<Result> {

    @Override
    public Result deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
      String value = je.getAsString();
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
}
