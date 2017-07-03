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

import sc2toolkit.replay.model.IUpgradeCommand;

/**
 * Describes a StarCraft II unit upgrade command.
 * 
 * <p>
 * The id of the upgrade command will be the id of the upgrade.
 * </p>
 * 
 * @author Andras Belicza
 */
public class UpgradeCommand extends Command implements IUpgradeCommand {
	
	/**
	 * Creates a new {@link UpgradeCommand}.
	 * 
	 * @param abilId ability id this upgrade command belongs to
	 */
	public UpgradeCommand( final String abilId ) {
		super( abilId );
	}
	
}
