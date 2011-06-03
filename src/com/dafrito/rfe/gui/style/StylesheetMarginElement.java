/**
 * 
 */
package com.dafrito.rfe.gui.style;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetMarginElement implements Nodeable {
	private int magnitude;

	public StylesheetMarginElement(int magnitude) {
		this.magnitude = magnitude;
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Margin Stylesheet-Element", "Magnitude: " + this.magnitude);
	}

	@Override
	public String toString() {
		return "margin";
	}

}