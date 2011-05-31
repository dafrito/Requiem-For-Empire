/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetMarginElement extends StylesheetElement implements Nodeable {
	private int magnitude;

	public StylesheetMarginElement(int magnitude) {
		this.magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " margin";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Margin Stylesheet-Element", "Magnitude: " + this.magnitude);
	}
}