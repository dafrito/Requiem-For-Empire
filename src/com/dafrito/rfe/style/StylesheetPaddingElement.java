/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetPaddingElement extends StylesheetElement implements Nodeable {
	private int magnitude;

	public StylesheetPaddingElement(int magnitude) {
		this.magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " padding";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Padding Stylesheet-Element", "Magnitude: " + this.magnitude);
	}
}