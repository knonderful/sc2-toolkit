/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.model;

/**
 * Describes a StarCraft II unit train command.
 * <p>
 * <p>
 * The id of the train command will be the id of the trained unit.
 * </p>
 *
 * @author Andras Belicza
 *
 * @see ICommand
 */
public interface ITrainCommand extends ICommand {

  /**
   * Tells if this train command is a worker train command (training a Drone,
   * Probe or SCV).
   *
   * @return true if this train command is a worker train command; false
   *         otherwise
   */
  boolean isWorker();

}
