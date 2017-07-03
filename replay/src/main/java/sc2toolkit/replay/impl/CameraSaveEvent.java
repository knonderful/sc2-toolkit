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

import sc2toolkit.common.Env;
import java.util.Map;
import sc2toolkit.replay.model.ICameraSaveEvent;
import sc2toolkit.replay.model.IRepProcessor;

/**
 * Camera save event.
 * 
 * @author Andras Belicza
 */
public class CameraSaveEvent extends Event implements ICameraSaveEvent {
	
	/** Target point of the camera. */
	private CamTargetPoint        targetPoint;
	
	
	/**
	 * Creates a new {@link CameraSaveEvent}.
	 * 
	 * @param struct event data structure
	 * @param id id of the event
	 * @param name type name of the event
	 * @param loop game loop when the event occurred
	 * @param userId user id causing the event
	 */
	public CameraSaveEvent( final Map< String, Object > struct, final int id, final String name, final int loop, final int userId ) {
		super( struct, id, name, loop, userId );
	}
	
	@Override
    public Integer getWhich() {
		return get( F_WHICH );
	}
	
	@Override
    public Map< String, Object > getTarget() {
		return get( F_TARGET );
	}
	
	@Override
    public CamTargetPoint getTargetPoint() {
		if ( targetPoint == null ) {
			final Map< String, Object > target = getTarget();
			if ( target != null )
				targetPoint = new CamTargetPoint( target );
		}
		
		return targetPoint;
	}
	
	@Override
	public String getParameters( final IRepProcessor repProc ) {
		final StringBuilder sb = new StringBuilder();
		
		sb.append( "Key=" ).append( 1 + getWhich() );
		sb.append( "; x=" ).append( Env.LANG.formatNumber( getTargetPoint().getXFloat(), 3 ) );
		sb.append( "; y=" ).append( Env.LANG.formatNumber( getTargetPoint().getYFloat(), 3 ) );
		
		return sb.toString();
	}
	
}
