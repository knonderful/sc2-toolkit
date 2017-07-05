/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc2toolkit.game.client;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sc2toolkit.game.client.model.GameState;
import sc2toolkit.game.client.model.UiState;

/**
 * An SC2 application state tracker. Its main function is to notify listeners
 * with state changes.
 */
public class Sc2StateTracker {

  private static final Logger LOG = Logger.getLogger(Sc2StateTracker.class.getName());

  private final Sc2StateResolver stateResolver = new Sc2StateResolver();
  private final Collection<Sc2AppStateListener> changeListeners = new LinkedList<>();
  private UiState lastUiState;
  private GameState lastGameState;

  public void addChangeListener(Sc2AppStateListener listener) {
    changeListeners.add(listener);
  }

  public void removeChangeListener(Sc2AppStateListener listener) {
    changeListeners.remove(listener);
  }

  public void update() {
    UiState uiState = null;
    GameState gameState = null;
    try {
      uiState = stateResolver.getUiState();
      try {
        gameState = stateResolver.getGameState();
      } catch (IOException e) {
        LOG.log(Level.FINE, "Could not retrieve game state.", e);
      }
    } catch (IOException e) {
      LOG.log(Level.FINE, "Could not retrieve UI state.", e);
    } finally {
      nextStates(uiState, gameState);
    }
  }

  private void nextStates(UiState uiState, GameState gameState) {
    changeListeners.forEach(listener -> listener.updateStates(lastUiState, uiState, lastGameState, gameState));
    lastUiState = uiState;
    lastGameState = gameState;
  }
}
