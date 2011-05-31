/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.Referenced;

class Exception_Nodeable_IncomparableObject extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488728539343927636L;

	public Exception_Nodeable_IncomparableObject(Referenced ref) {
		super(ref);
	}

	@Override
	public String getName() {
		return "This object has no inherent numeric value and is not directly comparable";
	}
}