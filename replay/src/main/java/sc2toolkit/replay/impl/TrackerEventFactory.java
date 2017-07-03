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

import java.util.Map;
import static sc2toolkit.replay.model.ITrackerEvents.ID_PLAYER_SETUP;
import static sc2toolkit.replay.model.ITrackerEvents.ID_PLAYER_STATS;
import static sc2toolkit.replay.model.ITrackerEvents.ID_UNIT_BORN;
import static sc2toolkit.replay.model.ITrackerEvents.ID_UNIT_DONE;
import static sc2toolkit.replay.model.ITrackerEvents.ID_UNIT_INIT;
import static sc2toolkit.replay.model.ITrackerEvents.ID_UPGRADE;

/**
 * Event factory that produces events from the tracker events stream data
 * structures.
 *
 * @author Andras Belicza
 */
public class TrackerEventFactory extends EventFactory {

  /**
   * Base build of the replay being parsed.
   */
  private final int baseBuild;

  /**
   * Creates a new {@link TrackerEventFactory}.
   */
  public TrackerEventFactory(final Header header) {
    baseBuild = header.baseBuild;
  }

  @Override
  public Event create(final Map< String, Object> struct, final int id, final String name, final int loop, final int userId) {
    switch (id) {
      case ID_PLAYER_STATS:
        return new PlayerStatsEvent(struct, id, name, loop, userId);
      case ID_UNIT_BORN:
        return new UnitBornEvent(struct, id, name, loop, userId, baseBuild);
      case ID_UPGRADE:
        return new UpgradeEvent(struct, id, name, loop, userId);
      case ID_UNIT_INIT:
        return new UnitInitEvent(struct, id, name, loop, userId, baseBuild);
      case ID_UNIT_DONE:
        return new UnitDoneEvent(struct, id, name, loop, userId);
      case ID_PLAYER_SETUP:
        return new PlayerSetupEvent(struct, id, name, loop, userId);
    }

    return super.create(struct, id, name, loop, userId);
  }

}
