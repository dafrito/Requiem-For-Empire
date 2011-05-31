/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetPaddingElement implements Nodeable {
	private int magnitude;

	public StylesheetPaddingElement(int magnitude) {
		this.magnitude = magnitude;
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Padding Stylesheet-Element", "Magnitude: " + this.magnitude);
	}

	@Override
	public String toString() {
		return "padding";
	}

}