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
import sc2toolkit.replay.model.IRepProcessor;
import sc2toolkit.replay.model.IResourceRequestFulfillEvent;

/**
 * Resource request fulfill event (a player approving a resource request and
 * sending the requested resources).
 *
 * @author Andras Belicza
 */
public class ResourceRequestFulfillEvent extends Event implements IResourceRequestFulfillEvent {

  /**
   * Fulfill request id field name.
   */
  public static final String F_FULFILL_REQUEST_ID = "fulfillRequestId";

  /**
   * Creates a new {@link ResourceRequestFulfillEvent}.
   *
   * @param struct event data structure
   * @param id     id of the event
   * @param name   type name of the event
   * @param loop   game loop when the event occurred
   * @param userId user id causing the event
   */
  public ResourceRequestFulfillEvent(final Map< String, Object> struct, final int id, final String name, final int loop, final int userId) {
    super(struct, id, name, loop, userId);
  }

  @Override
  public Integer getFulfillRequestId() {
    return get(F_FULFILL_REQUEST_ID);
  }

  @Override
  public String getParameters(final IRepProcessor repProc) {
    return "fulfill request id=" + getFulfillRequestId();
  }

}
