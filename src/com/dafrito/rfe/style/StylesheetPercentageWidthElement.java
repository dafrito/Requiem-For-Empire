/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetPercentageWidthElement extends StylesheetWidthElement implements Nodeable {
	private Double magnitude;

	public StylesheetPercentageWidthElement(double magnitude) {
		this.magnitude = new Double(magnitude);
	}

	@Override
	public String getElementName() {
		return " percentage-width";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Percentage Width Stylesheet-Element", "Percentage: " + this.magnitude);
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = new Double(magnitude);
	}
}