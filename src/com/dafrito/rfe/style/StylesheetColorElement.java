/**
 * 
 */
package com.dafrito.rfe.style;

import java.awt.Color;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetColorElement extends StylesheetElement implements Nodeable {
	private Color color;

	public StylesheetColorElement(Color color) {
		this.color = color;
	}

	public StylesheetColorElement(String colorString) {
		this.color = Stylesheets.getColor(colorString);
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Color Stylesheet-Element", "Color: " + Stylesheets.getColorName(this.color));
	}

	@Override
	public String toString() {
		return "color";
	}
}