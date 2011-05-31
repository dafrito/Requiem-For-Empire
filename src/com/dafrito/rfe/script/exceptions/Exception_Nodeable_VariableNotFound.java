/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.script.Referenced;

public class Exception_Nodeable_VariableNotFound extends Exception_Nodeable {
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
		assert Debugger.addNode("The variable, " + this.name + ", was not found");
	}

	@Override
	public String getName() {
		return "Variable Not Found (" + this.name + ")";
	}
}