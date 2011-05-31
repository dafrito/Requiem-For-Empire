/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetFontSizeElement extends StylesheetElement implements Nodeable {
	private int fontSize;

	public StylesheetFontSizeElement(int fontSize) {
		this.fontSize = fontSize;
	}

	@Override
	public String getElementName() {
		return " font-size";
	}

	public int getFontSize() {
		return this.fontSize;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Font Size Stylesheet-Element", "Font size: " + this.fontSize);
	}
}