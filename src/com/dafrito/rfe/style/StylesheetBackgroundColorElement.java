/**
 * 
 */
package com.dafrito.rfe.style;

import java.awt.Color;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetBackgroundColorElement extends StylesheetElement implements Nodeable {
	private Color color;

	public StylesheetBackgroundColorElement(Color backgroundColor) {
		this.color = backgroundColor;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Background Color Stylesheet-Element");
		assert Debugger.addNode("Color: " + Stylesheets.getColorName(this.color));
		assert Debugger.closeNode();
	}

	@Override
	public String toString() {
		return "background-color";
	}

}