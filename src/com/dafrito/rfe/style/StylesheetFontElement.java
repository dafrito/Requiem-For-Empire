/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetFontElement implements Nodeable {
	private String fontName;

	public StylesheetFontElement(String fontName) {
		this.fontName = fontName;
	}

	public String getFontName() {
		return this.fontName;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Font Stylesheet-Element", "Font Name: " + this.fontName);
	}

	@Override
	public String toString() {
		return " font";
	}

}