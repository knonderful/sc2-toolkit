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
import sc2toolkit.replay.model.IPlayerSetupEvent;

/**
 * Player setup tracker event.
 * 
 * <p>
 * Present since base build 27950 (2.1 PTR).
 * </p>
 * 
 * @author Andras Belicza
 */
public class PlayerSetupEvent extends Event implements IPlayerSetupEvent {
	
	/**
	 * Creates a new {@link PlayerSetupEvent}.
	 * 
	 * @param struct event data structure
	 * @param id id of the event
	 * @param name type name of the event
	 * @param loop game loop when the event occurred
	 * @param userId user id causing the event
	 */
	public PlayerSetupEvent( final Map< String, Object > struct, final int id, final String name, final int loop, final int userId ) {
		super( struct, id, name, loop, userId );
	}
	
	@Override
	public Integer getType() {
		return get( F_TYPE );
	}
	
	@Override
	public Integer getSetupUserId() {
		return get( F_USER_ID );
	}
	
	@Override
	public Integer getSetupSlotId() {
		return get( F_SLOT_ID );
	}
	
}
