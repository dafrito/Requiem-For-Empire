/**
 * 
 */
package com.dafrito.rfe.gui.style;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class StylesheetFontStyleElement {
	private int style;

	public StylesheetFontStyleElement(int style) {
		this.style = style;
	}

	@Inspectable
	public int getStyle() {
		return this.style;
	}

	@Override
	public String toString() {
		return "font-style";
	}

}