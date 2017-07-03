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
import sc2toolkit.replay.model.IResourceTradeEvent;

/**
 * Resource trade event (a player sending resources to another).
 * 
 * @author Andras Belicza
 */
public class ResourceTradeEvent extends Event implements IResourceTradeEvent {
	
	/**
	 * Creates a new {@link ResourceTradeEvent}.
	 * 
	 * @param struct event data structure
	 * @param id id of the event
	 * @param name type name of the event
	 * @param loop game loop when the event occurred
	 * @param userId user id causing the event
	 */
	public ResourceTradeEvent( final Map< String, Object > struct, final int id, final String name, final int loop, final int userId ) {
		super( struct, id, name, loop, userId );
	}
	
	@Override
	public Integer getRecipientId() {
		return get( F_RECIPIENT_ID );
	}
	
	@Override
	public Integer[] getResources() {
		return get( F_RESOURCES );
	}
	
	@Override
	public String getParameters( final IRepProcessor repProc ) {
		final Integer[] resources = getResources();
		return "recipient=" + repProc.getUsersByPlayerId()[ getRecipientId() ].getFullName() + "; minerals=" + resources[ 0 ] + "; gas=" + resources[ 1 ];
	}
	
}
