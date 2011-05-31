/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.script.proxies.FauxTemplate_Color;

public class Exception_Nodeable_InvalidColorRange extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7383423633507695338L;
	private Number invalid;
	private FauxTemplate_Color template;

	public Exception_Nodeable_InvalidColorRange(FauxTemplate_Color template, Number num) {
		super(template.getEnvironment());
		this.template = template;
		this.invalid = num;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The number provided cannot be decoded to create a valid color (" + this.invalid + ")");
		assert Debugger.addSnapNode("Template", this.template);
	}

	@Override
	public String getName() {
		return "Invalid Color Range (" + this.invalid + ")";
	}
}