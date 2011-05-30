/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetAbsoluteWidthElement extends StylesheetWidthElement implements Nodeable {
	private Integer magnitude;

	public StylesheetAbsoluteWidthElement(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute width";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Absolute Width Stylesheet-Element");
		assert Debugger.addNode("Width: " + this.magnitude);
		assert Debugger.closeNode();
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}
}