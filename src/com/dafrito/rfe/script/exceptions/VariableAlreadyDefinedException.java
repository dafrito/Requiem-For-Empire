/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;

public class VariableAlreadyDefinedException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8437355990865082053L;
	private String name;
	private ScriptTemplate_Abstract template;

	public VariableAlreadyDefinedException(Referenced elem, ScriptTemplate_Abstract template, String name) {
		super(elem);
		this.template = template;
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addSnapNode("The variable, " + this.name + ", has already been defined in the corresponding template", this.template);
	}

	@Override
	public String getName() {
		return "Predefined Variable (" + this.name + ")";
	}
}