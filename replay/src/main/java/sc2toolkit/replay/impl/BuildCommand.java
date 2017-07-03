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

import sc2toolkit.replay.model.IBuildCommand;

/**
 * Describes a StarCraft II build command.
 * <p>
 * <p>
 * The id of the build command will be the id of the built unit.
 * </p>
 *
 * @author Andras Belicza
 */
public class BuildCommand extends Command implements IBuildCommand {

  /**
   * Creates a new {@link BuildCommand}.
   *
   * @param abilId ability id this build command belongs to
   */
  public BuildCommand(final String abilId) {
    super(abilId);
  }

}
