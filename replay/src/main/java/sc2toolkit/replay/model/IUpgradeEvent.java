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
 * Upgrade tracker event.
 * 
 * @author Andras Belicza
 */
public interface IUpgradeEvent extends IEvent {
	
	/** Upgrade type name field name. */
	String F_UPGRADE_TYPE_NAME = "upgradeTypeName";
	
	
	/**
	 * Returns the upgradeTypeName.
	 * 
	 * @return the upgradeTypeName
	 */
	IXString getUpgradeTypeName();
	
}
