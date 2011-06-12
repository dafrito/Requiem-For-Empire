/**
 * 
 */
package com.dafrito.rfe.gui.style;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class StylesheetFontElement {
	private String fontName;

	public StylesheetFontElement(String fontName) {
		this.fontName = fontName;
	}

	@Inspectable
	public String getFontName() {
		return this.fontName;
	}

	@Override
	public String toString() {
		return " font";
	}

}