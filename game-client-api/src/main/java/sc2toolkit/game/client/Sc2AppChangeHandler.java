/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc2toolkit.game.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import sc2toolkit.game.client.model.GameState;
import sc2toolkit.game.client.model.Player;
import sc2toolkit.game.client.model.Result;
import sc2toolkit.game.client.model.UiState;

/**
 * A {@link Sc2AppStateListener} that handles SC2 application state changes and
 * invokes the corresponding methods in {@link Sc2ApplicationEventHandler}.
 */
public class Sc2AppChangeHandler implements Sc2AppStateListener {

  private final Sc2ApplicationEventHandler events;

  /**
   * Creates a new instance.
   *
   * @param events The event receiver.
   */
  public Sc2AppChangeHandler(Sc2ApplicationEventHandler events) {
    this.events = events;
  }

  @Override
  public void updateStates(UiState uiOld, UiState uiNew, GameState gameOld, GameState gameNew) {
    boolean wasInSc2;
    boolean isInSc2;
    boolean wasInGame;
    boolean isInGame;

    if (uiOld == null) {
      wasInSc2 = false;
      wasInGame = false;
    } else {
      wasInSc2 = true;
      wasInGame = uiOld.isInGame();
    }

    if (uiNew == null) {
      isInSc2 = false;
      isInGame = false;
    } else {
      isInSc2 = true;
      isInGame = uiNew.isInGame();
    }

    if (!isInSc2) {
      if (wasInSc2) {
        events.endSc2();
      }
      return;
    }

    if (!wasInSc2 && isInSc2) {
      events.startSc2();
      events.enterMenus();
    }

    if (gameNew == null) {
      return;
    }

    gameOld = Optional.ofNullable(gameOld)
            .orElse(new GameState(false, 0, Collections.emptyList()));
    Collection<Player> playersOld = gameOld.getPlayers();
    Collection<Player> playersNew = gameNew.getPlayers();

    if (!wasInGame && isInGame) {
      if (gameNew.isReplay()) {
        events.enterReplay(playersNew);
      } else {
        events.enterGame(playersNew);
      }
    }

    if (wasInGame && !isInGame) {
      events.enterMenus();
    }

    double timeOld = gameOld.getDisplayTime();
    double timeNew = gameNew.getDisplayTime();
    if (timeOld != timeNew) {
      events.updateDisplayTime(timeNew);
    }

    // Check for result changes, unless we're in a replay
    if (!gameNew.isReplay()) {
      // This is a very "thin" check to get around exceptions
      if (playersOld.size() == playersNew.size()) {
        Iterator<Player> playerOldIt = playersOld.iterator();
        Iterator<Player> playerNewIt = playersNew.iterator();
        while (playerNewIt.hasNext()) {
          Player playerOld = playerOldIt.next();
          Result resultOld;
          if (playerOld == null) {
            resultOld = Result.UNDECIDED;
          } else {
            resultOld = playerOld.getResult();
          }

          Player playerNew = playerNewIt.next();
          Result resultNew = playerNew.getResult();
          if (resultOld == Result.UNDECIDED) {
            switch (resultNew) {
              case VICTORY:
                events.playerWon(playerNew);
                break;
              case DEFEAT:
                events.playerLost(playerNew);
                break;
              case TIE:
                events.playerTied(playerNew);
                break;
            }
          }
        }
      }
    }
  }
}
