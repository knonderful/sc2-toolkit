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
import static sc2toolkit.replay.model.IMessageEvents.ID_CHAT;
import static sc2toolkit.replay.model.IMessageEvents.ID_PING;

/**
 * Event factory that produces events from the message events stream data
 * structures.
 *
 * @author Andras Belicza
 */
public class MessageEventFactory extends EventFactory {

  @Override
  public Event create(final Map< String, Object> struct, final int id, final String name, final int loop, final int userId) {
    switch (id) {
      case ID_CHAT:
        return new ChatEvent(struct, id, name, loop, userId);
      case ID_PING:
        return new PingEvent(struct, id, name, loop, userId);
    }

    return super.create(struct, id, name, loop, userId);
  }

}
