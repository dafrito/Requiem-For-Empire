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
 * @see Object
 */
public enum StylesheetProperty {
	WIDTH("Width") {
		@Override
		public Object defaultValue() {
			return new StylesheetPercentageWidthElement(1);
		}
	},
	HEIGHT("Height") {
		@Override
		public Object defaultValue() {
			return new StylesheetPercentageHeightElement(1);
		}
	},
	COLOR("Text Color") {
		@Override
		public Object defaultValue() {
			return Color.white;
		}
	},
	BACKGROUNDCOLOR("Background Color") {
		@Override
		public Object defaultValue() {
			return new StylesheetBackgroundColorElement(Color.black);
		}
	},
	FONTNAME("Font Name") {
		@Override
		public Object defaultValue() {
			return new StylesheetFontElement("Lucida Sans Unicode");
		}
	},
	FONTSIZE("Font Size") {
		@Override
		public Object defaultValue() {
			return new StylesheetFontSizeElement(18);
		}
	},
	FONTSTYLE("Font Style") {
		@Override
		public Object defaultValue() {
			return new StylesheetFontStyleElement(Font.PLAIN);
		}
	},

	BORDERBOTTOM("Bottom Border") {
		@Override
		public Object defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERTOP("Top Border") {
		@Override
		public Object defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERLEFT("Left Border") {
		@Override
		public Object defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},
	BORDERRIGHT("Right Border") {
		@Override
		public Object defaultValue() {
			return new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
		}
	},

	MARGINBOTTOM("Bottom Margin") {
		@Override
		public Object defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINTOP("Top Margin") {
		@Override
		public Object defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINLEFT("Left Margin") {
		@Override
		public Object defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},
	MARGINRIGHT("Right Margin") {
		@Override
		public Object defaultValue() {
			return new StylesheetMarginElement(0);
		}
	},

	PADDINGBOTTOM("Bottom Padding") {
		@Override
		public Object defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGTOP("Top Padding") {
		@Override
		public Object defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGLEFT("Left Padding") {
		@Override
		public Object defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	},
	PADDINGRIGHT("Right Padding") {
		@Override
		public Object defaultValue() {
			return new StylesheetPaddingElement(0);
		}
	};

	private final String name;

	private StylesheetProperty(String name) {
		this.name = name;
	}

	public Object defaultValue() {
		throw new UnsupportedOperationException("Style element has no default");
	}

	@Override
	public String toString() {
		return this.name;
	}

}