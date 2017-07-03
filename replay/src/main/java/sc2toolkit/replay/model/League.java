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

import java.util.Comparator;

/**
 * Battle.net league.
 * 
 * @author Andras Belicza
 */
public enum League {
	
	/** Unknown. */
	UNKNOWN( "Unknown" ),
	
	/** Bronze. */
	BRONZE( "Bronze" ),
	
	/** Silver. */
	SILVER( "Silver" ),
	
	/** Gold. */
	GOLD( "Gold" ),
	
	/** Platinum. */
	PLATINUM( "Platinum" ),
	
	/** Diamond. */
	DIAMOND( "Diamond" ),
	
	/** Master. */
	MASTER( "Master" ),
	
	/** Grandmaster. */
	GRANDMASTER( "Grandmaster" ),
	
	/** Unranked. */
	UNRANKED( "Unranked" );
	
	
	/** Text value of the league. */
	public final String              text;
	
	/**
	 * League letter (first character of the English name except <code>'R'</code> for {@link #GRANDMASTER} and <code>'-'</code> for {@link #UNKNOWN}).
	 */
	public final char                letter;
	
	/**
	 * Creates a new {@link League}.
	 * 
	 * @param text text value
	 * @param ricon ricon of the league
	 */
	private League( final String text ) {
		this.text = text;
		letter = "UNKNOWN".equals( name() ) ? '-' : Character.toUpperCase( "GRANDMASTER".equals( name() ) ? 'R' : text.charAt( 0 ) );
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public char getLetter() {
		return letter;
	}
	
	/** Cache of the values array. */
	public static final League[]                                      VALUES                 = values();
	
	/** A comparator which returns a more meaningful league order. */
	public static final Comparator< League >                         COMPARATOR             = new Comparator< League >() {
		                                                                                         @Override
		                                                                                         public int compare( final League l1, final League l2 ) {
			                                                                                         if ( l1 == l2 )
				                                                                                         return 0;
			                                                                                         
			                                                                                         // Make unknown the least
			                                                                                         if ( l1 == UNKNOWN )
				                                                                                         return -1;
			                                                                                         if ( l2 == UNKNOWN )
				                                                                                         return 1;
			                                                                                         
			                                                                                         // Then comes unranked
			                                                                                         if ( l1 == UNRANKED )
				                                                                                         return -1;
			                                                                                         if ( l2 == UNRANKED )
				                                                                                         return 1;
			                                                                                         
			                                                                                         // Rest is good in their natural order
			                                                                                         return l1.ordinal() - l2.ordinal();
		                                                                                         }
	                                                                                         };
	
}
