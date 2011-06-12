/**
 * 
 */
package com.dafrito.rfe.gui.style;

import java.awt.Color;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public class StylesheetBorderElement {
	private int magnitude;
	private ScriptKeywordType style;
	private Color color;

	public StylesheetBorderElement(int mag, ScriptKeywordType style, Color color) {
		this.magnitude = mag;
		this.style = style;
		this.color = color;
	}

	@Inspectable
	public Color getColor() {
		return this.color;
	}

	@Inspectable
	public int getMagnitude() {
		return this.magnitude;
	}

	@Inspectable
	public ScriptKeywordType getStyle() {
		return this.style;
	}

	@Override
	public String toString() {
		return "border";
	}
}