import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class InterfaceElement implements Nodeable, GraphicalElement, ScriptConvertible {
	private Stylesheet m_classStylesheet, m_uniqueStylesheet;
	private int m_xAnchor, m_yAnchor;
	private Interface_Container m_parent;
	private ScriptEnvironment m_environment;

	public InterfaceElement(ScriptEnvironment environment, Stylesheet uniqueStylesheet, Stylesheet classStylesheet) {
		m_environment = environment;
		m_uniqueStylesheet = uniqueStylesheet;
		m_classStylesheet = classStylesheet;
	}

	public void addXAnchor(int addingAmount) {
		m_xAnchor += addingAmount;
	}

	public void addYAnchor(int addingAmount) {
		m_yAnchor += addingAmount;
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_InterfaceElement elem = new FauxTemplate_InterfaceElement(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING));
		elem.setElement(this);
		return elem;
	}

	public Color getBackgroundColor() {
		return ((StylesheetBackgroundColorElement) getStyleElement(StylesheetElementType.BACKGROUNDCOLOR)).getColor();
	}

	public int getBottomBorderMagnitude() {
		return ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude();
	}

	public int getBottomFluffMagnitude() {
		return getBottomMarginMagnitude() + getBottomBorderMagnitude() + getBottomPaddingMagnitude();
	}

	public int getBottomMarginMagnitude() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
	}

	public int getBottomPaddingMagnitude() {
		return ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude();
	}

	public Stylesheet getClassStylesheet() {
		return m_classStylesheet;
	}

	public Font getCurrentFont() {
		return new Font(((StylesheetFontElement) getStyleElement(StylesheetElementType.FONTNAME)).getFontName(), ((StylesheetFontStyleElement) getStyleElement(StylesheetElementType.FONTSTYLE)).getStyle(), ((StylesheetFontSizeElement) getStyleElement(StylesheetElementType.FONTSIZE)).getFontSize());
	}

	public Color getCurrentTextColor() {
		return ((StylesheetColorElement) getStyleElement(StylesheetElementType.COLOR)).getColor();
	}

	@Override
	public Rectangle getDrawingBounds() {
		return new Rectangle(m_xAnchor, m_yAnchor, getInternalWidth() - 1, getInternalHeight() - 1);
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public int getFullHeight() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude() + ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude() + ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude() + getInternalHeight() + ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude() + ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude() + ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
	}

	public int getFullWidth() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude() + ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude() + ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude() + getInternalWidth() + ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude() + ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude() + ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
	}

	public int getHorizontalFluffMagnitude() {
		return getLeftFluffMagnitude() + getRightFluffMagnitude();
	}

	public int getInternalHeight() {
		StylesheetHeightElement element = (StylesheetHeightElement) getStyleElement(StylesheetElementType.HEIGHT);
		if (element instanceof StylesheetAbsoluteHeightElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * getParent().getContainerElement().getInternalHeight() - getVerticalFluffMagnitude());
		}
	}

	public int getInternalWidth() {
		StylesheetWidthElement element = (StylesheetWidthElement) getStyleElement(StylesheetElementType.WIDTH);
		if (element instanceof StylesheetAbsoluteWidthElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * getParent().getContainerElement().getInternalWidth() - getHorizontalFluffMagnitude());
		}
	}

	public int getLeftBorderMagnitude() {
		return ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude();
	}

	public int getLeftFluffMagnitude() {
		return getLeftMarginMagnitude() + getLeftBorderMagnitude() + getLeftPaddingMagnitude();
	}

	public int getLeftMarginMagnitude() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude();
	}

	public int getLeftPaddingMagnitude() {
		return ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude();
	}

	public StylesheetElement getNonRecursiveStyleElement(StylesheetElementType code) {
		StylesheetElement element = null;
		if (getUniqueStylesheet() != null) {
			element = getUniqueStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		if (getClassStylesheet() != null) {
			element = getClassStylesheet().getElement(code);
		}
		return element;
	}

	@Override
	public Interface_Container getParent() {
		return m_parent;
	}

	public int getRightBorderMagnitude() {
		return ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude();
	}

	public int getRightFluffMagnitude() {
		return getRightMarginMagnitude() + getRightBorderMagnitude() + getRightPaddingMagnitude();
	}

	public int getRightMarginMagnitude() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
	}

	public int getRightPaddingMagnitude() {
		return ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude();
	}

	public InterfaceElement_Root getRoot() {
		if (getParent() == null) {
			return null;
		}
		return getParent().getContainerElement().getRoot();
	}

	public StylesheetElement getStyleElement(StylesheetElementType code) {
		StylesheetElement element = null;
		if (getUniqueStylesheet() != null) {
			element = getUniqueStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		if (getClassStylesheet() != null) {
			element = getClassStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		return getParent().getContainerElement().getStyleElement(code);
	}

	public int getTopBorderMagnitude() {
		return ((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude();
	}

	public int getTopFluffMagnitude() {
		return getTopMarginMagnitude() + getTopBorderMagnitude() + getTopPaddingMagnitude();
	}

	public int getTopMarginMagnitude() {
		return ((StylesheetMarginElement) getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude();
	}

	public int getTopPaddingMagnitude() {
		return ((StylesheetPaddingElement) getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude();
	}

	public Stylesheet getUniqueStylesheet() {
		return m_uniqueStylesheet;
	}

	public int getVerticalFluffMagnitude() {
		return getTopFluffMagnitude() + getBottomFluffMagnitude();
	}

	public int getXAnchor() {
		return m_xAnchor;
	}

	public int getYAnchor() {
		return m_yAnchor;
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Interface Element");
		assert Debugger.addSnapNode("Unique Stylesheet", m_uniqueStylesheet);
		assert Debugger.addSnapNode("Class Stylesheet", m_classStylesheet);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		if (!((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERLEFT)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERLEFT)).getColor());
			int xPos = getXAnchor() + getLeftMarginMagnitude();
			int yPos = getYAnchor() + getTopMarginMagnitude();
			int width = getLeftBorderMagnitude();
			int height = getTopBorderMagnitude() + getTopPaddingMagnitude() + getInternalHeight() + getBottomPaddingMagnitude() + getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERRIGHT)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERRIGHT)).getColor());
			int xPos = getXAnchor() + getLeftFluffMagnitude() + getInternalWidth() + getRightPaddingMagnitude();
			int yPos = getYAnchor() + getTopMarginMagnitude();
			int width = getRightBorderMagnitude();
			int height = getTopBorderMagnitude() + getTopPaddingMagnitude() + getInternalHeight() + getBottomPaddingMagnitude() + getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERTOP)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERTOP)).getColor());
			int xPos = getXAnchor() + getLeftMarginMagnitude();
			int yPos = getYAnchor() + getTopMarginMagnitude();
			int width = getLeftBorderMagnitude() + getLeftPaddingMagnitude() + getInternalWidth() + getRightPaddingMagnitude() + getRightBorderMagnitude();
			int height = getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERBOTTOM)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) getStyleElement(StylesheetElementType.BORDERBOTTOM)).getColor());
			int xPos = getXAnchor() + getLeftMarginMagnitude();
			int yPos = getYAnchor() + getTopFluffMagnitude() + getInternalHeight() + getBottomPaddingMagnitude();
			int width = getLeftBorderMagnitude() + getLeftPaddingMagnitude() + getInternalWidth() + getRightPaddingMagnitude() + getRightBorderMagnitude();
			int height = getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		addXAnchor(getLeftFluffMagnitude());
		addYAnchor(getTopFluffMagnitude());
		g2d.setColor(getBackgroundColor());
		g2d.fill(new Rectangle(getXAnchor(), getYAnchor(), getInternalWidth(), getInternalHeight()));
		g2d.setFont(getCurrentFont());
		g2d.setColor(getCurrentTextColor());
	}

	public void setClassStylesheet(Stylesheet sheet) {
		m_classStylesheet = sheet;
	}

	@Override
	public void setParent(Interface_Container container) {
		m_parent = container;
	}

	@Override
	public void setPreferredWidth(int width) {
		assert Debugger.openNode("Setting Preferred Width (" + width + ")");
		assert Debugger.addSnapNode("Element", this);
		if (null == getUniqueStylesheet()) {
			m_uniqueStylesheet = new Stylesheet(m_environment);
			m_uniqueStylesheet.setUnique(true);
		}
		getUniqueStylesheet().addElement(StylesheetElementType.WIDTH, new StylesheetAbsoluteWidthElement(width));
		Debugger.closeNode();
	}

	public void setUniqueStylesheet(Stylesheet sheet) {
		m_uniqueStylesheet = sheet;
	}

	@Override
	public void setXAnchor(int xAnchor) {
		m_xAnchor = xAnchor;
	}

	@Override
	public void setYAnchor(int yAnchor) {
		m_yAnchor = yAnchor;
	}
}
