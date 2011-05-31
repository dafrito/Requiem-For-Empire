/**
 * 
 */
package com.dafrito.rfe.style;

import java.awt.Color;
import java.awt.Font;

import com.dafrito.rfe.script.ScriptKeywordType;

/**
 * Represents the set of stylesheet properties.
 * 
 * @author Aaron Faanes
 * 
 * @see Stylesheet
 * @see StylesheetElement
 */
public enum StylesheetElementType {
	WIDTH("Width") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPercentageWidthElement(1);
		}
	},
	HEIGHT("Height") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPercentageHeightElement(1);
		}
	},
	COLOR("Text Color") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetColorElement(Color.white);
		}
	},
	BACKGROUNDCOLOR("Background Color") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetBackgroundColorElement(Color.black);
		}
	},
	FONTNAME("Font Name") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetFontElement("Lucida Sans Unicode");
		}
	},
	FONTSIZE("Font Size") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetFontSizeElement(18);
		}
	},
	FONTSTYLE("Font Style") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetFontStyleElement(Font.PLAIN);
		}
	},

	BORDERBOTTOM("Bottom Border") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERTOP("Top Border") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERLEFT("Left Border") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERRIGHT("Right Border") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},

	MARGINBOTTOM("Bottom Margin") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINTOP("Top Margin") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINLEFT("Left Margin") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINRIGHT("Right Margin") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},

	PADDINGBOTTOM("Bottom Padding") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGTOP("Top Padding") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGLEFT("Left Padding") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGRIGHT("Right Padding") {
		@Override
		public StylesheetElement defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	};

	private final String name;

	private StylesheetElementType(String name) {
		this.name = name;
	}

	public StylesheetElement defaultValue() {
		throw new UnsupportedOperationException("Style element has no default");
	}

	@Override
	public String toString() {
		return this.name;
	}

}