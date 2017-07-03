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
import sc2toolkit.replay.model.IResourceRequestEvent;

/**
 * Resource request event (a player requesting resources).
 * 
 * @author Andras Belicza
 */
public class ResourceRequestEvent extends Event implements IResourceRequestEvent {
	
	/**
	 * Creates a new {@link ResourceRequestEvent}.
	 * 
	 * @param struct event data structure
	 * @param id id of the event
	 * @param name type name of the event
	 * @param loop game loop when the event occurred
	 * @param userId user id causing the event
	 */
	public ResourceRequestEvent( final Map< String, Object > struct, final int id, final String name, final int loop, final int userId ) {
		super( struct, id, name, loop, userId );
	}
	
	@Override
	public Integer[] getResources() {
		return get( F_RESOURCES );
	}
	
	@Override
	public String getParameters( final IRepProcessor repProc ) {
		final Integer[] resources = getResources();
		return "minerals=" + resources[ 0 ] + "; gas=" + resources[ 1 ];
	}
	
}
