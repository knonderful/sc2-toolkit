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

import sc2toolkit.replay.model.IAttribute;

/**
 * Describes an attribute in the attribute events.
 * 
 * @author Andras Belicza
 */
public class Attribute implements IAttribute {
	
	/** Attribute namespace. */
	public Integer namespace;
	
	/** Attribute id. */
	public Integer id;
	
	/** Attribute scope. */
	public Integer scope;
	
	/** Attribute value. */
	public String  value;
	
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public Integer getNamespace() {
		return namespace;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public Integer getScope() {
		return scope;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
}
