/**
 * 
 */
package com.dafrito.rfe.style;

import java.awt.Color;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.ScriptKeywordType;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetBorderElement extends StylesheetElement implements Nodeable {
	private int magnitude;
	private ScriptKeywordType style;
	private Color color;

	public StylesheetBorderElement(int mag, ScriptKeywordType style, Color color) {
		this.magnitude = mag;
		this.style = style;
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String getElementName() {
		return " border";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	public ScriptKeywordType getStyle() {
		return this.style;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Border Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + this.magnitude);
		assert Debugger.addNode("Style: " + this.style);
		assert Debugger.addNode("Color: " + Stylesheets.getColorName(this.color));
		assert Debugger.closeNode();
	}
}