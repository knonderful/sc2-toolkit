package sc2toolkit.app.toolkit;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import sc2toolkit.game.client.Sc2AppChangeHandler;
import sc2toolkit.game.client.Sc2AppStateListener;
import sc2toolkit.game.client.Sc2ApplicationEvents;
import sc2toolkit.game.client.Sc2StateTracker;
import sc2toolkit.game.client.model.Player;

public class Main extends Application {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());
  private static final String NOT_RUNNING = "SC2 not running";
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Sc2StateTracker stateTracker = new Sc2StateTracker();
  private final Sc2AppStateListener changeListener = new Sc2AppChangeHandler(new Sc2EventsImpl());
  private Label state;
  private TextField playerNameInput;
  private MediaPlayer victoryPlayer;
  private MediaPlayer defeatPlayer;
  private String name;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void stop() throws Exception {
    stopMonitoring();
  }

  @Override
  public void init() throws Exception {
    victoryPlayer = createPlayer("won.mp3");
    defeatPlayer = createPlayer("lost.mp3");
  }

  @Override
  public void start(Stage stage) throws Exception {
    GridPane rootGrid = new GridPane();
    rootGrid.setAlignment(Pos.CENTER_LEFT);
    rootGrid.setHgap(10);
    rootGrid.setVgap(10);
    rootGrid.setPadding(new Insets(25, 25, 25, 25));

    Label stateLabel = new Label("Current state:");
    rootGrid.add(stateLabel, 0, 0);

    state = new Label(NOT_RUNNING);
    rootGrid.add(state, 1, 0, 2, 1);

    Label playerNameLabel = new Label("Player:");
    rootGrid.add(playerNameLabel, 0, 1);
    playerNameInput = new TextField();
    rootGrid.add(playerNameInput, 1, 1, 2, 1);

    playerNameInput.textProperty().addListener((ignore, oldValue, newValue) -> {
      name = newValue;
    });

    Label victoryLabel = new Label("Victory:");
    rootGrid.add(victoryLabel, 0, 2);

    Button victoryPlayButton = new Button("Play");
    victoryPlayButton.onActionProperty().set(event -> rewindAndPlay(victoryPlayer));
    rootGrid.add(victoryPlayButton, 1, 2);

    Slider victorySlider = new Slider(0, 1, victoryPlayer.getVolume());
    victorySlider.valueProperty().addListener((ignore, oldValue, newValue) -> victoryPlayer.setVolume(newValue.doubleValue()));
    rootGrid.add(victorySlider, 2, 2);

    Label defeatLabel = new Label("Defeat:");
    rootGrid.add(defeatLabel, 0, 3);

    Button defeatPlayButton = new Button("Play");
    defeatPlayButton.onActionProperty().set(event -> rewindAndPlay(defeatPlayer));
    rootGrid.add(defeatPlayButton, 1, 3);

    Slider defeatSlider = new Slider(0, 1, defeatPlayer.getVolume());
    defeatSlider.valueProperty().addListener((ignore, oldValue, newValue) -> defeatPlayer.setVolume(newValue.doubleValue()));
    rootGrid.add(defeatSlider, 2, 3);

    Scene scene = new Scene(rootGrid);
    stage.setTitle("SC2 Tracker");
    stage.setScene(scene);
    stage.show();

    startMonitoring();
  }

  private static MediaPlayer createPlayer(String filename) {
    File file = new File(filename);
    Media song = new Media(file.toURI().toString());
    MediaPlayer player = new MediaPlayer(song);
    return player;
  }

  private void startMonitoring() {
    stateTracker.addChangeListener(changeListener);

    scheduler.scheduleAtFixedRate(this::updateState, 0, 1, TimeUnit.SECONDS);
  }

  private void stopMonitoring() {
    stateTracker.removeChangeListener(changeListener);

    scheduler.shutdown();
    try {
      scheduler.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      LOG.log(Level.WARNING, "Scheduler did not shut down within 10 seconds.", ex);
      scheduler.shutdownNow();
    }
  }

  private void updateState() {
    try {
      stateTracker.update();
    } catch (Exception e) {
      // TODO: remove this once the code is stable... =)
      e.printStackTrace();
      throw e;
    }
  }

  private void setStateText(String text) {
    Platform.runLater(() -> {
      state.setText(text);
    });
  }

  private void playVictory() {
    rewindAndPlay(victoryPlayer);
  }

  private void playDefeat() {
    rewindAndPlay(defeatPlayer);
  }

  private static void rewindAndPlay(MediaPlayer player) {
    player.seek(Duration.ZERO);
    player.play();
  }

  private class Sc2EventsImpl implements Sc2ApplicationEvents {

    @Override
    public void startSc2() {
      setStateText("In menus");
    }

    @Override
    public void endSc2() {
      setStateText(NOT_RUNNING);
    }

    @Override
    public void enterMenus() {
      setStateText("In menus");
    }

    @Override
    public void enterGame(Collection<Player> players) {
      setStateText("In game");
    }

    @Override
    public void enterReplay(Collection<Player> players) {
      setStateText("In replay");
    }

    @Override
    public void updateDisplayTime(double displayTime) {
      // Not used currently
      //System.out.printf("Display time: %f%n", displayTime);
    }

    @Override
    public void playerWon(Player player) {
      if (isTargetedPlayer(player)) {
        playVictory();
      }
    }

    @Override
    public void playerLost(Player player) {
      if (isTargetedPlayer(player)) {
        playDefeat();
      }
    }

    @Override
    public void playerTied(Player player) {
      // Nothing to be done
    }

    private boolean isTargetedPlayer(Player player) {
      if (name == null) {
        return false;
      }

      String otherName = player.getName();
      if (otherName == null) {
        LOG.fine(() -> String.format("No name defined for player: %s.", player));
        return false;
      }

      return name.equals(otherName);
    }
  }
}