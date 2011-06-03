/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptFunction_Abstract;

public class Exception_Nodeable_IllegalNullReturnValue extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6453503758041260366L;
	private ScriptFunction_Abstract function;

	public Exception_Nodeable_IllegalNullReturnValue(Referenced ref, ScriptFunction_Abstract fxn) {
		super(ref);
		this.function = fxn;
	}

	public Exception_Nodeable_IllegalNullReturnValue(ScriptEnvironment env, ScriptFunction_Abstract fxn) {
		super(env);
		this.function = fxn;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("This function is attempting to return implicitly, even though it is of type, " + this.function.getReturnType(), this.function);
	}

	@Override
	public String getName() {
		return "Illegal Null Return Value";
	}
}