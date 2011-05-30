/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetAbsoluteHeightElement extends StylesheetHeightElement implements Nodeable {
	private Integer magnitude;

	public StylesheetAbsoluteHeightElement(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute height";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Absolute Height Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + this.magnitude);
		assert Debugger.closeNode();
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}
}