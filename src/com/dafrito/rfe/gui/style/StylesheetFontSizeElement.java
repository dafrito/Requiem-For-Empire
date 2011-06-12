/**
 * 
 */
package com.dafrito.rfe.gui.style;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class StylesheetFontSizeElement {
	private int fontSize;

	public StylesheetFontSizeElement(int fontSize) {
		this.fontSize = fontSize;
	}

	@Inspectable
	public int getFontSize() {
		return this.fontSize;
	}

	@Override
	public String toString() {
		return "font-size";
	}

}