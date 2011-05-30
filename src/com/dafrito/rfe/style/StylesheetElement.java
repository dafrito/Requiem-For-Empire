package com.dafrito.rfe.style;

import com.dafrito.rfe.Exception_InternalError;



public abstract class StylesheetElement {
	public static String getCodeName(StylesheetElementType code) {
		// ADD TO ROOT ELEMENT!! // ADD TO ROOT ELEMENT!! // DO THIS NOW!! // YES, REALLY, NOW!! // MWAHA! CAPS!
		switch (code) {
		case WIDTH:
			return "Width";
		case HEIGHT:
			return "Height";
		case COLOR:
			return "Text Color";
		case FONTNAME:
			return "Font Name";
		case FONTSIZE:
			return "Font Size";
		case FONTSTYLE:
			return "Font Style";
		case BORDERBOTTOM:
			return "Bottom Border";
		case BORDERTOP:
			return "Top Border";
		case BORDERLEFT:
			return "Left Border";
		case BORDERRIGHT:
			return "Right Border";
		case MARGINBOTTOM:
			return "Bottom Margin";
		case MARGINTOP:
			return "Top Margin";
		case MARGINLEFT:
			return "Left Margin";
		case MARGINRIGHT:
			return "Right Margin";
		case PADDINGBOTTOM:
			return "Bottom Padding";
		case PADDINGTOP:
			return "Top Padding";
		case PADDINGLEFT:
			return "Left Padding";
		case PADDINGRIGHT:
			return "Right Padding";
		case BACKGROUNDCOLOR:
			return "Background Color";
		}
		throw new Exception_InternalError("Invalid default");
	}

	public abstract String getElementName();
}
