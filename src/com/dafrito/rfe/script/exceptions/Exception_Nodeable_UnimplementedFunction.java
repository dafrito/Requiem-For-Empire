/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;

public class Exception_Nodeable_UnimplementedFunction extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8027051981016874329L;
	private ScriptTemplate_Abstract template;
	private String name;

	public Exception_Nodeable_UnimplementedFunction(ScriptEnvironment env, ScriptTemplate_Abstract template, String name) {
		super(env);
		this.template = template;
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The abstract function, " + this.name + ", is unimplemented", this.template);
	}

	@Override
	public String getName() {
		return "Unimplemented Abstract Function (" + this.name + ")";
	}
}