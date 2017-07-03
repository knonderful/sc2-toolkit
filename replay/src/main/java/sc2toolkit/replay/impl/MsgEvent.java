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
import sc2toolkit.replay.model.IMsgEvent;
import sc2toolkit.replay.model.Recipient;

/**
 * General message event.
 *
 * @author Andras Belicza
 */
public class MsgEvent extends Event implements IMsgEvent {

  /**
   * Creates a new {@link MsgEvent}.
   *
   * @param struct event data structure
   * @param id     id of the event
   * @param name   type name of the event
   * @param loop   game loop when the event occurred
   * @param userId user id causing the event
   */
  public MsgEvent(final Map< String, Object> struct, final int id, final String name, final int loop, final int userId) {
    super(struct, id, name, loop, userId);
  }

  @Override
  public Recipient getRecipient() {
    return Recipient.fromRawValue(this.< Integer>get(F_RECIPIENT));
  }

}
