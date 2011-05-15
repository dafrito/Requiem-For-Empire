import java.awt.Color;

class StylesheetAbsoluteHeightElement extends StylesheetHeightElement implements Nodeable {
	private Integer m_magnitude;

	public StylesheetAbsoluteHeightElement(int magnitude) {
		m_magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute height";
	}

	@Override
	public Object getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Absolute Height Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + m_magnitude);
		assert Debugger.closeNode();
		return true;
	}

	public void setMagnitude(int magnitude) {
		m_magnitude = new Integer(magnitude);
	}
}

class StylesheetAbsoluteWidthElement extends StylesheetWidthElement implements Nodeable {
	private Integer m_magnitude;

	public StylesheetAbsoluteWidthElement(int magnitude) {
		m_magnitude = new Integer(magnitude);
	}

	@Override
	public String getElementName() {
		return "n absolute width";
	}

	@Override
	public Object getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Absolute Width Stylesheet-Element");
		assert Debugger.addNode("Width: " + m_magnitude);
		assert Debugger.closeNode();
		return true;
	}

	public void setMagnitude(int magnitude) {
		m_magnitude = new Integer(magnitude);
	}
}

// Color elements
class StylesheetBackgroundColorElement extends StylesheetElement implements Nodeable {
	private Color m_color;

	public StylesheetBackgroundColorElement(Color backgroundColor) {
		m_color = backgroundColor;
	}

	public Color getColor() {
		return m_color;
	}

	@Override
	public String getElementName() {
		return " background-color";
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Background Color Stylesheet-Element");
		assert Debugger.addNode("Color: " + RiffJavaToolbox.getColorName(m_color));
		assert Debugger.closeNode();
		return true;
	}
}

class StylesheetBorderElement extends StylesheetElement implements Nodeable {
	private int m_magnitude;
	private ScriptKeywordType m_style;
	private Color m_color;

	public StylesheetBorderElement(int mag, ScriptKeywordType style, Color color) {
		m_magnitude = mag;
		m_style = style;
		m_color = color;
	}

	public Color getColor() {
		return m_color;
	}

	@Override
	public String getElementName() {
		return " border";
	}

	public int getMagnitude() {
		return m_magnitude;
	}

	public ScriptKeywordType getStyle() {
		return m_style;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Border Stylesheet-Element");
		assert Debugger.addNode("Magnitude: " + m_magnitude);
		assert Debugger.addNode("Style: " + m_style);
		assert Debugger.addNode("Color: " + RiffJavaToolbox.getColorName(m_color));
		assert Debugger.closeNode();
		return true;
	}
}

class StylesheetColorElement extends StylesheetElement implements Nodeable {
	private Color m_color;

	public StylesheetColorElement(Color color) {
		m_color = color;
	}

	public StylesheetColorElement(String colorString) {
		m_color = RiffJavaToolbox.getColor(colorString);
	}

	public Color getColor() {
		return m_color;
	}

	@Override
	public String getElementName() {
		return " color";
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Color Stylesheet-Element", "Color: " + RiffJavaToolbox.getColorName(m_color));
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
	private String m_fontName;

	public StylesheetFontElement(String fontName) {
		m_fontName = fontName;
	}

	@Override
	public String getElementName() {
		return " font";
	}

	public String getFontName() {
		return m_fontName;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Stylesheet-Element", "Font Name: " + m_fontName);
		return true;
	}
}

class StylesheetFontSizeElement extends StylesheetElement implements Nodeable {
	private int m_fontSize;

	public StylesheetFontSizeElement(int fontSize) {
		m_fontSize = fontSize;
	}

	@Override
	public String getElementName() {
		return " font-size";
	}

	public int getFontSize() {
		return m_fontSize;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Size Stylesheet-Element", "Font size: " + m_fontSize);
		return true;
	}
}

class StylesheetFontStyleElement extends StylesheetElement implements Nodeable {
	private int m_style;

	public StylesheetFontStyleElement(int style) {
		m_style = style;
	}

	@Override
	public String getElementName() {
		return " font-style";
	}

	public int getStyle() {
		return m_style;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Font Style Stylesheet-Element", "Font-Size: " + RiffJavaToolbox.getFontStyleName(m_style));
		return true;
	}
}

// Height elements
abstract class StylesheetHeightElement extends StylesheetElement {
	public abstract Object getMagnitude();
}

// Box model elements
class StylesheetMarginElement extends StylesheetElement implements Nodeable {
	private int m_magnitude;

	public StylesheetMarginElement(int magnitude) {
		m_magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " margin";
	}

	public int getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Margin Stylesheet-Element", "Magnitude: " + m_magnitude);
		return true;
	}
}

class StylesheetPaddingElement extends StylesheetElement implements Nodeable {
	private int m_magnitude;

	public StylesheetPaddingElement(int magnitude) {
		m_magnitude = magnitude;
	}

	@Override
	public String getElementName() {
		return " padding";
	}

	public int getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Padding Stylesheet-Element", "Magnitude: " + m_magnitude);
		return true;
	}
}

class StylesheetPercentageHeightElement extends StylesheetHeightElement implements Nodeable {
	private Double m_magnitude;

	public StylesheetPercentageHeightElement(double magnitude) {
		m_magnitude = new Double(magnitude);
	}

	@Override
	public String getElementName() {
		return " percentage-height";
	}

	@Override
	public Object getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Percentage Height Stylesheet-Element", "Percentage: " + m_magnitude);
		return true;
	}

	public void setMagnitude(double magnitude) {
		m_magnitude = new Double(magnitude);
	}
}

class StylesheetPercentageWidthElement extends StylesheetWidthElement implements Nodeable {
	private Double m_magnitude;

	public StylesheetPercentageWidthElement(double magnitude) {
		m_magnitude = new Double(magnitude);
	}

	@Override
	public String getElementName() {
		return " percentage-width";
	}

	@Override
	public Object getMagnitude() {
		return m_magnitude;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.addSnapNode("Percentage Width Stylesheet-Element", "Percentage: " + m_magnitude);
		return true;
	}

	public void setMagnitude(double magnitude) {
		m_magnitude = new Double(magnitude);
	}
}

// Width elements
abstract class StylesheetWidthElement extends StylesheetElement {
	public abstract Object getMagnitude();
}
