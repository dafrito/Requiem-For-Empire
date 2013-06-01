/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class Exception_Nodeable_VariableNotFound extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3774953699349441078L;
	private String name;

	public Exception_Nodeable_VariableNotFound(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The variable, " + this.name + ", was not found");
	}

	@Override
	public String getName() {
		return "Variable Not Found (" + this.name + ")";
	}
}