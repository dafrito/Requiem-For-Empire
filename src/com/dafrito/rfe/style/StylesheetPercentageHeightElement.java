/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetPercentageHeightElement extends StylesheetHeightElement implements Nodeable {
	private Double magnitude;

	public StylesheetPercentageHeightElement(double magnitude) {
		this.magnitude = new Double(magnitude);
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Percentage Height Stylesheet-Element", "Percentage: " + this.magnitude);
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = new Double(magnitude);
	}

	@Override
	public String toString() {
		return "percentage-height";
	}

}