/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.parsing.Referenced;

public class Exception_Nodeable_VariableTypeNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6495019820246931939L;
	private String type;

	public Exception_Nodeable_VariableTypeNotFound(Referenced ref, String type) {
		super(ref);
		this.type = type;
	}

	public Exception_Nodeable_VariableTypeNotFound(ScriptEnvironment env, String type) {
		super(env);
		this.type = type;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The variable type, " + this.type + ", was not found");
	}

	@Override
	public String getName() {
		return "Undefined Variable-Type (" + this.type + ")";
	}
}