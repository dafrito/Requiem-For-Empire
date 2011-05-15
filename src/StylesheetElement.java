import java.awt.Color;

class StylesheetAbsoluteHeightElement extends StylesheetHeightElement implements Nodeable {
	private Integer magnitude;

	public StylesheetAbsoluteHeightElement(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute height";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Absolute Height Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + this.magnitude);
		assert Debugger.closeNode();
		return true;
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}
}

class StylesheetAbsoluteWidthElement extends StylesheetWidthElement implements Nodeable {
	private Integer magnitude;

	public StylesheetAbsoluteWidthElement(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute width";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Absolute Width Stylesheet-Element");
		assert Debugger.addNode("Width: " + this.magnitude);
		assert Debugger.closeNode();
		return true;
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = new Integer(magnitude);
	}
}

// Color elements
class StylesheetBackgroundColorElement extends StylesheetElement implements Nodeable {
	private Color color;

	public StylesheetBackgroundColorElement(Color backgroundColor) {
		this.color = backgroundColor;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String getElementName() {
		return " background-color";
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Background Color Stylesheet-Element");
		assert Debugger.addNode("Color: " + RiffJavaToolbox.getColorName(this.color));
		assert Debugger.closeNode();
		return true;
	}
}

class StylesheetBorderElement extends StylesheetElement implements Nodeable {
	private int magnitude;
	private ScriptKeywordType style;
	private Color color;

	public StylesheetBorderElement(int mag, ScriptKeywordType style, Color color) {
		this.magnitude = mag;
		this.style = style;
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String getElementName() {
		return " border";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	public ScriptKeywordType getStyle() {
		return this.style;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Border Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + this.magnitude);
		assert Debugger.addNode("Style: " + this.style);
		assert Debugger.addNode("Color: " + RiffJavaToolbox.getColorName(this.color));
		assert Debugger.closeNode();
		return true;
	}
}

class StylesheetColorElement extends StylesheetElement implements Nodeable {
	private Color color;

	public StylesheetColorElement(Color color) {
		this.color = color;
	}

	public StylesheetColorElement(String colorString) {
		this.color = RiffJavaToolbox.getColor(colorString);
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String getElementName() {
		return " color";
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Color Stylesheet-Element", "Color: " + RiffJavaToolbox.getColorName(this.color));
		return true;
	}
}

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

enum StylesheetElementType {
	WIDTH, HEIGHT,
	COLOR, BACKGROUNDCOLOR,
	FONTNAME, FONTSIZE, FONTSTYLE,
	BORDERBOTTOM, BORDERTOP, BORDERLEFT, BORDERRIGHT,
	MARGINBOTTOM, MARGINTOP, MARGINLEFT, MARGINRIGHT,
	PADDINGBOTTOM, PADDINGTOP, PADDINGLEFT, PADDINGRIGHT
}

// Font elements
class StylesheetFontElement extends StylesheetElement implements Nodeable {
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
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Stylesheet-Element", "Font Name: " + this.fontName);
		return true;
	}
}

class StylesheetFontSizeElement extends StylesheetElement implements Nodeable {
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
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Size Stylesheet-Element", "Font size: " + this.fontSize);
		return true;
	}
}

class StylesheetFontStyleElement extends StylesheetElement implements Nodeable {
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
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Style Stylesheet-Element", "Font-Size: " + RiffJavaToolbox.getFontStyleName(this.style));
		return true;
	}
}

// Height elements
abstract class StylesheetHeightElement extends StylesheetElement {
	public abstract Object getMagnitude();
}

// Box model elements
class StylesheetMarginElement extends StylesheetElement implements Nodeable {
	private int magnitude;

	public StylesheetMarginElement(int magnitude) {
		this.magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " margin";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Margin Stylesheet-Element", "Magnitude: " + this.magnitude);
		return true;
	}
}

class StylesheetPaddingElement extends StylesheetElement implements Nodeable {
	private int magnitude;

	public StylesheetPaddingElement(int magnitude) {
		this.magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " padding";
	}

	public int getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Padding Stylesheet-Element", "Magnitude: " + this.magnitude);
		return true;
	}
}

class StylesheetPercentageHeightElement extends StylesheetHeightElement implements Nodeable {
	private Double magnitude;

	public StylesheetPercentageHeightElement(double magnitude) {
		this.magnitude = new Double(magnitude);
	}

	@Override
	public String getElementName() {
		return " percentage-height";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Percentage Height Stylesheet-Element", "Percentage: " + this.magnitude);
		return true;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = new Double(magnitude);
	}
}

class StylesheetPercentageWidthElement extends StylesheetWidthElement implements Nodeable {
	private Double magnitude;

	public StylesheetPercentageWidthElement(double magnitude) {
		this.magnitude = new Double(magnitude);
	}

	@Override
	public String getElementName() {
		return " percentage-width";
	}

	@Override
	public Object getMagnitude() {
		return this.magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Percentage Width Stylesheet-Element", "Percentage: " + this.magnitude);
		return true;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = new Double(magnitude);
	}
}

// Width elements
abstract class StylesheetWidthElement extends StylesheetElement {
	public abstract Object getMagnitude();
}
