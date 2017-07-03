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
 * General message event.
 * 
 * @author Andras Belicza
 */
public interface IMsgEvent extends IEvent {
	
	/** Message recipient event field name. */
	String F_RECIPIENT = "recipient";
	
	
	/**
	 * Returns the recipient of the message event.
	 * 
	 * @return the recipient of the message event
	 */
	Recipient getRecipient();
	
}
