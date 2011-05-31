/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;

public class Exception_Nodeable_VariableAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8437355990865082053L;
	private String name;
	private ScriptTemplate_Abstract template;

	public Exception_Nodeable_VariableAlreadyDefined(Referenced elem, ScriptTemplate_Abstract template, String name) {
		super(elem);
		this.template = template;
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The variable, " + this.name + ", has already been defined in the corresponding template", this.template);
	}

	@Override
	public String getName() {
		return "Predefined Variable (" + this.name + ")";
	}
}