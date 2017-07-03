/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.impl;

import sc2toolkit.balancedata.BalanceData;
import java.util.Map;
import sc2toolkit.common.exception.TkResourceException;
import sc2toolkit.replay.model.Controller;
import sc2toolkit.replay.model.IReplay;

/**
 * Class representing the data of a StarCraft II replay file.
 *
 * @author Andras Belicza
 */
public class Replay implements IReplay {

  /**
   * Header info of the replay.
   */
  public final Header header;

  /**
   * Details of the replay.
   */
  public final Details details;

  /**
   * Init data of the replay.
   */
  public final InitData initData;

  /**
   * Attributes events of the replay.
   */
  public final AttributesEvents attributesEvents;

  /**
   * Message events of the replay.
   */
  public final MessageEvents messageEvents;

  /**
   * Game events of the replay.
   */
  public final GameEvents gameEvents;

  /**
   * Tracker events of the replay.
   */
  public final TrackerEvents trackerEvents;

  /**
   * The <b>playerId &rArr; userId</b> mapping in the form of an array where the
   * index is the player id and the value is the user id.
   */
  private final int[] playerIdUserIdMap;

  public Replay(Header header, Details details, InitData initData, AttributesEvents attributesEvents, MessageEvents messageEvents, GameEvents gameEvents, TrackerEvents trackerEvents, int[] playerIdUserIdMap) throws TkResourceException {
    this.header = header;
    this.details = details;
    this.initData = initData;
    this.attributesEvents = attributesEvents;
    this.messageEvents = messageEvents;
    this.gameEvents = gameEvents;
    this.trackerEvents = trackerEvents;
    this.balanceData = createBalanceData(header);
    this.playerIdUserIdMap = playerIdUserIdMap;
  }

  public static BalanceData createBalanceData(Header header) throws TkResourceException {
    return BalanceData.get(header.getVersionView());
  }

  /**
   * Lazily initialized balance data.
   */
  private BalanceData balanceData;

  @Override
  public Header getHeader() {
    return header;
  }

  @Override
  public Details getDetails() {
    return details;
  }

  @Override
  public InitData getInitData() {
    return initData;
  }

  @Override
  public AttributesEvents getAttributesEvents() {
    return attributesEvents;
  }

  @Override
  public MessageEvents getMessageEvents() {
    return messageEvents;
  }

  @Override
  public GameEvents getGameEvents() {
    return gameEvents;
  }

  @Override
  public TrackerEvents getTrackerEvents() {
    return trackerEvents;
  }

  public int[] getPlayerIdUserIdMap() {
    return playerIdUserIdMap;
  }

  /**
   * Returns the <b>playerId &rArr; userId</b> mapping in the form of an array
   * where the index is the player id and the value is the user id.
   * <p>
   * <p>
   * The player id is the id found in events in "old" replays and in the tracker
   * events in new replays.<br>
   * This player id is not the player index of the players array found in the
   * Details (there might be discrepancy).
   * </p>
   * <p>
   * <p>
   * This method can only be called if details, init data and attributes events
   * have already been parsed!
   * </p>
   *
   * @return the <b>playerId &rArr; userId</b> mapping
   */
  public static int[] createPlayerIdUserIdMap(InitData initData, AttributesEvents attributesEvents) {
      int[] playerIdUserIdMap = new int[17]; // Allow +1 because player id is 1-based

      final Slot[] slots = initData.getLobbyState().getSlots();
      final Map< Integer, Map< Integer, Attribute>> scopes = attributesEvents.scopes;

      for (int slotIdx = 0, playerId = 0; slotIdx < slots.length; slotIdx++) {
        final Slot slot = slots[slotIdx];

        if (slot.getController() != Controller.HUMAN && slot.getController() != Controller.COMPUTER) {
          // Not a human or computer, "seemingly" not a player!

          // If a player drops during game start, his/her slot will be empty,
          // he/she will not have a player object in the details,
          // but attributes events will have a record of him/her, this is how we can detect this case.
          // In this case (and only in this case) playerId still have to be incremented!
          if (scopes != null && scopes.containsKey(Integer.valueOf(playerId + 1))) {
            // Found a player record in the attributes event => dropped player (during game start)
            // or player from slot have been moved by the game host and is left Open.
            // Increment playerId (but no user): doesn't matter what userId we assign,
            // there will be no event for this "missing" player.
            playerId++;
          }
          continue;
        }

        playerIdUserIdMap[++playerId] = slot.userId == null ? -1 : slot.userId;
    }

    return playerIdUserIdMap;
  }

  @Override
  public BalanceData getBalanceData() {
    return balanceData;
  }

}
