/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.Referenced;

public class Exception_Nodeable_TemplateAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7184264414947646959L;
	private String name;

	public Exception_Nodeable_TemplateAlreadyDefined(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + this.name + ", is already defined");
	}

	@Override
	public String getName() {
		return "Template Already Defined (" + this.name + ")";
	}
}