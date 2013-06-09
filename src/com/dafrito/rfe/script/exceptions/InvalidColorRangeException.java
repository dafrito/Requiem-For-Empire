/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.proxies.FauxTemplate_Color;

public class InvalidColorRangeException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7383423633507695338L;
	private Number invalid;
	private FauxTemplate_Color template;

	public InvalidColorRangeException(FauxTemplate_Color template, Number num) {
		super(template.getEnvironment());
		this.template = template;
		this.invalid = num;
	}

	@Override
	public void getExtendedInformation() {
		assert Logs.addNode("The number provided cannot be decoded to create a valid color (" + this.invalid + ")");
		assert Logs.addSnapNode("Template", this.template);
	}

	@Override
	public String getName() {
		return "Invalid Color Range (" + this.invalid + ")";
	}
}