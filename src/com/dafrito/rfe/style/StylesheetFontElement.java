/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetFontElement extends StylesheetElement implements Nodeable {
	private String fontName;

	public StylesheetFontElement(String fontName) {
		this.fontName = fontName;
	}

	@Override
	public String getElementName() {
		return " font";
	}

	public String getFontName() {
		return this.fontName;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Font Stylesheet-Element", "Font Name: " + this.fontName);
	}
}