/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.script.parsing.Referenced;

class IncomparableObjectException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488728539343927636L;

	public IncomparableObjectException(Referenced ref) {
		super(ref);
	}

	@Override
	public String getName() {
		return "This object has no inherent numeric value and is not directly comparable";
	}
}