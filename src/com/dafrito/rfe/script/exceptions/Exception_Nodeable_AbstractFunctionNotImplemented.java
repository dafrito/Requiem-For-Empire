/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Referenced;
import com.dafrito.rfe.script.ScriptFunction_Abstract;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;

public class Exception_Nodeable_AbstractFunctionNotImplemented extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8937024716489715221L;
	private ScriptTemplate_Abstract object;
	private ScriptFunction_Abstract function;

	public Exception_Nodeable_AbstractFunctionNotImplemented(Referenced ref, ScriptTemplate_Abstract object, ScriptFunction_Abstract function) {
		super(ref);
		this.object = object;
		this.function = function;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The template is not abstract and does not implement the following function", this.object);
		assert Debugger.addNode(this.function);
	}

	@Override
	public String getName() {
		return "Abstract Function Not Implememented";
	}
}