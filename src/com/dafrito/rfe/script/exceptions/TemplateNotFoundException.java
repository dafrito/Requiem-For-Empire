/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.parsing.Referenced;

public class TemplateNotFoundException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6044178726296381294L;
	private String name;

	public TemplateNotFoundException(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The template, " + this.name + ", was not found");
	}

	@Override
	public String getName() {
		return "Template Not Found (" + this.name + ")";
	}
}