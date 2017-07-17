package sc2toolkit.app.toolkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import sc2toolkit.app.toolkit.overwolf.OverwolfAppConnector;
import sc2toolkit.app.toolkit.overwolf.OverwolfAppConnectorFactory;
import sc2toolkit.game.client.Sc2AppChangeHandler;
import sc2toolkit.game.client.Sc2AppStateListener;
import sc2toolkit.game.client.Sc2StateTracker;
import sc2toolkit.game.client.model.Player;
import sc2toolkit.game.client.model.PlayerType;
import sc2toolkit.game.client.model.Race;
import sc2toolkit.game.client.model.Result;

public class Main extends Application {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());
  private static final String NOT_RUNNING = "SC2 not running";
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Sc2StateTracker stateTracker = new Sc2StateTracker();
  private final Sc2EventHandlerDispatcher sc2EventDispatcher = new Sc2EventHandlerDispatcher(
          new AnnouncementEventHandler(),
          new Sc2ClientStateEventHandler()
  );
  private final Sc2AppStateListener changeListener = new Sc2AppChangeHandler(sc2EventDispatcher);
  private Label state;
  private TextField playerNameInput;
  private MediaPlayer victoryPlayer;
  private MediaPlayer defeatPlayer;
  private String name;

  static {
    try (InputStream loggingConfig = Main.class.getResourceAsStream("/logging.properties")) {
      if (loggingConfig != null) {
        LogManager.getLogManager().readConfiguration(loggingConfig);
      } else {
        LOG.log(Level.WARNING, "No logging configuration found.");
      }
    } catch (IOException e) {
      LOG.log(Level.WARNING, "Could not load logging properties.", e);
    }
  }

  public static void main(String[] args) {
    LOG.log(Level.INFO, () -> String.format("Starting SC2 tool kit with arguments: %s.", Arrays.toString(args)));
    launch(args);
    LOG.log(Level.INFO, "Stopped SC2 tool kit.");
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
    GridPane generalGrid = createGeneralPage();
    GridPane announcementGrid = createAnnouncementPage();
    ShutdownNotifier shutdownObservable = new ShutdownNotifier();
    GridPane overwolfAppGrid = createOverwolfAppPage(shutdownObservable);

    Tab generalTab = new Tab("General", generalGrid);
    Tab announcementTab = new Tab("Announcement", announcementGrid);
    Tab overwolfAppTab = new Tab("Overwolf App", overwolfAppGrid);

    TabPane tabs = new TabPane(generalTab, announcementTab, overwolfAppTab);
    tabs.getSelectionModel().select(generalTab);
    tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    Scene scene = new Scene(tabs);
    stage.setTitle("SC2 Tracker");
    stage.setScene(scene);
    stage.show();

    startMonitoring();

    stage.setOnCloseRequest(event -> shutdownObservable.notifyShutdown());
  }

  private GridPane createGeneralPage() {
    GridPane generalGrid = new GridPane();
    generalGrid.setAlignment(Pos.TOP_LEFT);
    generalGrid.setHgap(10);
    generalGrid.setVgap(10);
    generalGrid.setPadding(new Insets(25, 25, 25, 25));

    Label stateLabel = new Label("Current state:");
    generalGrid.add(stateLabel, 0, 0);

    state = new Label(NOT_RUNNING);
    generalGrid.add(state, 1, 0, 2, 1);

    Label playerNameLabel = new Label("Player:");
    generalGrid.add(playerNameLabel, 0, 1);

    playerNameInput = new TextField();
    generalGrid.add(playerNameInput, 1, 1, 2, 1);

    playerNameInput.textProperty().addListener((ignore, oldValue, newValue) -> {
      name = newValue;
    });

    return generalGrid;
  }

  private GridPane createAnnouncementPage() {
    GridPane announcementGrid = new GridPane();
    announcementGrid.setAlignment(Pos.TOP_LEFT);
    announcementGrid.setHgap(10);
    announcementGrid.setVgap(10);
    announcementGrid.setPadding(new Insets(25, 25, 25, 25));

    Label victoryLabel = new Label("Victory:");
    announcementGrid.add(victoryLabel, 0, 2);

    Button victoryPlayButton = new Button("Play");
    victoryPlayButton.onActionProperty().set(event -> rewindAndPlay(victoryPlayer));
    announcementGrid.add(victoryPlayButton, 1, 2);

    Slider victorySlider = new Slider(0, 1, victoryPlayer.getVolume());
    victorySlider.valueProperty().addListener((ignore, oldValue, newValue) -> victoryPlayer.setVolume(newValue.doubleValue()));
    announcementGrid.add(victorySlider, 2, 2);

    Label defeatLabel = new Label("Defeat:");
    announcementGrid.add(defeatLabel, 0, 3);

    Button defeatPlayButton = new Button("Play");
    defeatPlayButton.onActionProperty().set(event -> rewindAndPlay(defeatPlayer));
    announcementGrid.add(defeatPlayButton, 1, 3);

    Slider defeatSlider = new Slider(0, 1, defeatPlayer.getVolume());
    defeatSlider.valueProperty().addListener((ignore, oldValue, newValue) -> defeatPlayer.setVolume(newValue.doubleValue()));
    announcementGrid.add(defeatSlider, 2, 3);

    return announcementGrid;
  }

  private GridPane createOverwolfAppPage(ShutdownNotifier exitObservable) {
    GridPane announcementGrid = new GridPane();
    announcementGrid.setAlignment(Pos.TOP_LEFT);
    announcementGrid.setHgap(10);
    announcementGrid.setVgap(10);
    announcementGrid.setPadding(new Insets(25, 25, 25, 25));

    OverwolfAppConnectorFactory factory = new OverwolfAppConnectorFactory(exitObservable);
    Button startButton = new Button("Start");
    Button pingButton = new Button("Ping");
    pingButton.setDisable(true);

    Player dummyPlayer = new Player(0, "Byun", PlayerType.USER, Race.TERRAN, Result.UNDECIDED);

    startButton.onActionProperty().set(event -> {
      InetAddress address;
      try {
        address = InetAddress.getByName("localhost");
      } catch (UnknownHostException ex) {
        throw new RuntimeException(ex);
      }

      startButton.setDisable(true);

      CompletionStage<OverwolfAppConnector> future = factory.create(new InetSocketAddress(address, 8989));
      future.whenComplete((connector, exception) -> {
        if (exception != null) {
          LOG.log(Level.WARNING, "Could not create Overwolf App connector.", exception);
          return;
        }

        pingButton.onActionProperty().set(evt -> {
          connector.startGame(dummyPlayer).whenComplete((nothing, cause) -> {
            if (cause != null) {
              LOG.log(Level.INFO, "Could not send message.", cause);
              return;
            }

            LOG.log(Level.INFO, "Message sent OK.");
          });
        });
        pingButton.setDisable(false);
      });
    });
    announcementGrid.add(startButton, 0, 0);
    announcementGrid.add(pingButton, 1, 0);

    return announcementGrid;
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
      LOG.log(Level.SEVERE, "Error while updating the SC2 state tracker.", e);
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

  private class Sc2ClientStateEventHandler extends AbstractSc2EventHandler {

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
  }

  private class AnnouncementEventHandler extends AbstractSc2EventHandler {

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
