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
import sc2toolkit.replay.model.IUpgradeEvent;

/**
 * Upgrade tracker event.
 *
 * @author Andras Belicza
 */
public class UpgradeEvent extends Event implements IUpgradeEvent {

  /**
   * Upgrade type name field name.
   */
  public static final String F_UPGRADE_TYPE_NAME = "upgradeTypeName";

  /**
   * Creates a new {@link UpgradeEvent}.
   *
   * @param struct event data structure
   * @param id     id of the event
   * @param name   type name of the event
   * @param loop   game loop when the event occurred
   * @param userId user id causing the event
   */
  public UpgradeEvent(final Map< String, Object> struct, final int id, final String name, final int loop, final int userId) {
    super(struct, id, name, loop, userId);
  }

  @Override
  public XString getUpgradeTypeName() {
    return get(F_UPGRADE_TYPE_NAME);
  }

}
