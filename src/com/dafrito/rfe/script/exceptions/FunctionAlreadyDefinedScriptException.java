/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.RiffScriptFunction;

public class FunctionAlreadyDefinedScriptException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734795857087945843L;
	private String name;

	public FunctionAlreadyDefinedScriptException(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + RiffScriptFunction.getDisplayableFunctionName(this.name) + ", is already defined");
	}

	@Override
	public String getName() {
		return "Function Already Defined (" + RiffScriptFunction.getDisplayableFunctionName(this.name) + ")";
	}
}