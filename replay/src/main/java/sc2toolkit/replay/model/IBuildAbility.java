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
 * Describes a StarCraft II build ability.
 * <p>
 * <p>
 * This is a "virtual" ability. The ability id will be the id of unit to whom
 * the command is given.
 * </p>
 *
 * @author Andras Belicza
 *
 * @see IAbility
 */
public interface IBuildAbility extends IAbility {

  // Nothing new is added yet, currently acting as a marker interface.
}
