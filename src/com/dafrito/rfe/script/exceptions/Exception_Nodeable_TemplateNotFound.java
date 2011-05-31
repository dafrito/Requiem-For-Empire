/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.script.Referenced;

public class Exception_Nodeable_TemplateNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6044178726296381294L;
	private String name;

	public Exception_Nodeable_TemplateNotFound(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + this.name + ", was not found");
	}

	@Override
	public String getName() {
		return "Template Not Found (" + this.name + ")";
	}
}