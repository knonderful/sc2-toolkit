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

import sc2toolkit.replay.model.ICommand;

/**
 * Describes an ability command.
 *
 * @author Andras Belicza
 */
public class Command extends Entity implements ICommand {

  /**
   * Ability id this command belongs to. This is stored redundant here for fast
   * access.
   */
  public String abilId;

  /**
   * Creates a new {@link Command}.
   *
   * @param abilId ability id this command belongs to
   */
  public Command(final String abilId) {
    this.abilId = abilId;
  }

  @Override
  public String getAbilId() {
    return abilId;
  }

}
