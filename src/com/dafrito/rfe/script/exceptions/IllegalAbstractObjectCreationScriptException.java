/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class IllegalAbstractObjectCreationScriptException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2947890390494988260L;

	public IllegalAbstractObjectCreationScriptException(Referenced ref) {
		super(ref);
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("An abstract object is trying to be instantiated.");
	}

	@Override
	public String getName() {
		return "Illegal Abstract Object Creation";
	}
}