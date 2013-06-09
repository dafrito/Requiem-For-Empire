/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class TemplateAlreadyDefinedException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7184264414947646959L;
	private String name;

	public TemplateAlreadyDefinedException(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The template, " + this.name + ", is already defined");
	}

	@Override
	public String getName() {
		return "Template Already Defined (" + this.name + ")";
	}
}