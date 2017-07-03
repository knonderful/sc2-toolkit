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

import java.util.List;
import sc2toolkit.replay.model.IEvent;
import sc2toolkit.replay.model.ITrackerEvents;

/**
 * StarCraft II Replay tracker events.
 *
 * @author Andras Belicza
 */
public class TrackerEvents implements ITrackerEvents {

  /**
   * Tracker event list.
   */
  public final Event[] events;

  /**
   * Creates a new {@link TrackerEvents}.
   *
   * @param eventList tracker event list
   */
  public TrackerEvents(final List< Event> eventList) {
    this.events = eventList.toArray(new Event[eventList.size()]);
  }

  @Override
  public IEvent[] getEvents() {
    return events;
  }

}
