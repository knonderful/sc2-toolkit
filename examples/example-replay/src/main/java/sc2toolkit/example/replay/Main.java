/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc2toolkit.example.replay;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import sc2toolkit.balancedata.BalanceData;
import sc2toolkit.common.exception.TkException;
import sc2toolkit.replay.impl.ChatEvent;
import sc2toolkit.replay.impl.Event;
import sc2toolkit.replay.impl.RepProcessor;
import sc2toolkit.replay.impl.Replay;
import sc2toolkit.replay.impl.User;
import sc2toolkit.replay.model.IEntity;
import sc2toolkit.replay.model.IEvent;
import sc2toolkit.replay.model.ITrackerEvents;
import sc2toolkit.replay.model.IUnitBornEvent;
import sc2toolkit.replay.model.IUnitInitEvent;
import sc2toolkit.replay.model.IUpgradeEvent;

/**
 * The following is just a simple program that uses the replay API for some
 * simple analysis.
 */
public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) throws TkException {
    Path mapPath = Paths.get("test.SC2Replay");
    System.out.println("FILE: " + mapPath.toAbsolutePath());
    RepProcessor proc = new RepProcessor(mapPath);
    System.out.printf("WINNERS: %s%n", proc.getWinnersString());
    System.out.printf("LOSERS : %s%n", proc.getLosersString());

    Replay replay = proc.getReplay();
    User[] users = proc.getUsersByUserId();

    System.out.println("===== CHAT LOG =====");
    for (Event event : replay.getMessageEvents().getEvents()) {
      if (!(event instanceof ChatEvent)) {
        continue;
      }
      ChatEvent chat = (ChatEvent) event;
      System.out.printf("%s: %s%n", users[chat.getUserId()].getName(), chat.getText());
    }
    System.out.println("====================");

// The following is the user commands; note that in SC2 units can be queued, which is why we can not use this for the build order extraction
//    Arrays.asList(replay.getGameEvents().getEvents()).stream()
//            .filter(ICmdEvent.class::isInstance)
//            .map(evt -> (ICmdEvent) evt)
//            .forEach(evt -> {
//              ICommand command = evt.getCommand();
//              if (!(command instanceof ITrainCommand)) {
//                return;
//              }
//              ITrainCommand trainCommand = (ITrainCommand) command;
//              System.out.printf("%s %-10s Train %s%n", proc.formatLoopTime(evt.getLoop()), users[evt.getUserId()].getName(), trainCommand.getId());
//            });
    User[] usersByPlayerId = proc.getUsersByPlayerId();

    BalanceData balanceData = replay.getBalanceData();
    // Since the type-change events don't contain the player information, but only the unit tag index of the command centers,
    // we have to track the CCs separately in order to correlate which type-change event belongs to which player.
    // Tag -> player ID
    Map<Integer, Integer> commandCenters = new HashMap<>(64);
    TreeSet<BuildOrderElement> boElts = new TreeSet<>();
    for (IEvent event : replay.getTrackerEvents().getEvents()) {
      int playerId;
      int startLoop;
      IEntity entity;

      switch (event.getId()) {
        case ITrackerEvents.ID_UNIT_INIT: {
          IUnitInitEvent uie = (IUnitInitEvent) event;
          playerId = uie.getControlPlayerId();
          String unitTypeName = uie.getUnitTypeName().toString();
          entity = balanceData.getUnit(unitTypeName);

          if (unitTypeName.equals("CommandCenter")) {
            commandCenters.put(uie.getUnitTagIndex(), uie.getControlPlayerId());
          }
          startLoop = event.getLoop();

          break;
        }
        case ITrackerEvents.ID_UNIT_BORN:
          IUnitBornEvent bornEvent = (IUnitBornEvent) event;
          playerId = bornEvent.getControlPlayerId();
          String unitTypeName = bornEvent.getUnitTypeName().toString();
          entity = balanceData.getUnit(unitTypeName);
          if (unitTypeName.equals("CommandCenter")) {
            commandCenters.put(bornEvent.getUnitTagIndex(), bornEvent.getControlPlayerId());
          }

          startLoop = bornEvent.getLoop() - (int) (entity.getCostTime() * 16);

          break;
        case ITrackerEvents.ID_UPGRADE: {
          playerId = event.getPlayerId();
          entity = balanceData.getUpgradeCmd(((IUpgradeEvent) event).getUpgradeTypeName().toString());
          if (entity == null) {
            // When would this happen? This was in the Scelight code...
            continue;
          }
          startLoop = event.getLoop() - (int) (entity.getCostTime() * 16);
          break;
        }
        case ITrackerEvents.ID_UNIT_TYPE_CHANGE:
          // TODO: The current version of the API does not properly support unit type change events
          continue;
        default:
          continue;
      }

      if (startLoop > 0) {
        boElts.add(new BuildOrderElement(playerId, startLoop, entity));
      }
    }

    System.out.printf("%d command centers were made.%n", commandCenters.size());

    boElts.forEach(boe -> System.out.printf("%s %-10s %s%n", proc.formatLoopTime(boe.getLoop()), usersByPlayerId[boe.getPlayerId()].getName(), boe.getEntity().getText()));
  }

  private static class BuildOrderElement implements Comparable<BuildOrderElement> {

    private final int playerId;
    private final int loop;
    private final IEntity entity;

    public BuildOrderElement(int playerId, int loop, IEntity entity) {
      this.playerId = playerId;
      this.loop = loop;
      this.entity = entity;
    }

    public int getPlayerId() {
      return playerId;
    }

    public int getLoop() {
      return loop;
    }

    public IEntity getEntity() {
      return entity;
    }

    @Override
    public int hashCode() {
      return loop;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
      return obj == this;
    }

    @Override
    public int compareTo(BuildOrderElement o) {
      if (equals(o)) {
        return 0;
      }

      if (loop < o.loop) {
        return -1;
      }

      // Intentionally not returning 0 if they are equal, since we don't want to break the equals()/Comparable contract
      return 1;
    }

  }
}
