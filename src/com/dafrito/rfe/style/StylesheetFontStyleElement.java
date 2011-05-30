/**
 * 
 */
package com.dafrito.rfe.style;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class StylesheetFontStyleElement extends StylesheetElement implements Nodeable {
	private int style;

	public StylesheetFontStyleElement(int style) {
		this.style = style;
	}

	@Override
	public String getElementName() {
		return " font-style";
	}

	public int getStyle() {
		return this.style;
	}

	@Override
	public void nodificate() {
		assert Debugger.addSnapNode("Font Style Stylesheet-Element", "Font-Size: " + Stylesheets.getFontStyleName(this.style));
	}
}