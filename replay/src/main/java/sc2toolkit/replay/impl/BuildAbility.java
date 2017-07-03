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

import sc2toolkit.replay.model.IBuildAbility;

/**
 * Describes a StarCraft II build ability.
 * 
 * <p>
 * This is a "virtual" ability. The ability id will be the id of unit to whom the command is given.
 * </p>
 * 
 * @author Andras Belicza
 */
public class BuildAbility extends Ability implements IBuildAbility {
	
	/**
	 * Creates a new {@link BuildAbility}.
	 * 
	 * @param id id of the build ability, it is the id of the unit to whom the command is given
	 */
	public BuildAbility( final String id ) {
		super( id );
	}
	
}
