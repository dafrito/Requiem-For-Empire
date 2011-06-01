/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptFunction;

public class Exception_Nodeable_FunctionAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734795857087945843L;
	private String name;

	public Exception_Nodeable_FunctionAlreadyDefined(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(this.name) + ", is already defined");
	}

	@Override
	public String getName() {
		return "Function Already Defined (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
	}
}