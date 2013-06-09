/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class VariableTypeAlreadyDefinedException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3105141324838401870L;
	private String type;

	public VariableTypeAlreadyDefinedException(Referenced ref, String type) {
		super(ref);
		this.type = type;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The variable type, " + this.type + ", has already been defined");
	}

	@Override
	public String getName() {
		return "Predefined Variable-Type (" + this.type + ")";
	}
}