package com.dafrito.rfe.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptKeywordType;
import com.dafrito.rfe.script.ScriptValueType;
import com.dafrito.rfe.script.proxies.FauxTemplate_InterfaceElement;
import com.dafrito.rfe.style.Stylesheet;
import com.dafrito.rfe.style.StylesheetAbsoluteHeightElement;
import com.dafrito.rfe.style.StylesheetAbsoluteWidthElement;
import com.dafrito.rfe.style.StylesheetBackgroundColorElement;
import com.dafrito.rfe.style.StylesheetBorderElement;
import com.dafrito.rfe.style.StylesheetColorElement;
import com.dafrito.rfe.style.StylesheetElement;
import com.dafrito.rfe.style.StylesheetElementType;
import com.dafrito.rfe.style.StylesheetFontElement;
import com.dafrito.rfe.style.StylesheetFontSizeElement;
import com.dafrito.rfe.style.StylesheetFontStyleElement;
import com.dafrito.rfe.style.StylesheetHeightElement;
import com.dafrito.rfe.style.StylesheetMarginElement;
import com.dafrito.rfe.style.StylesheetPaddingElement;
import com.dafrito.rfe.style.StylesheetWidthElement;

public class InterfaceElement implements Nodeable, GraphicalElement, ScriptConvertible {
	private Stylesheet classStylesheet, uniqueStylesheet;
	private int xAnchor, yAnchor;
	private Interface_Container parent;
	private ScriptEnvironment environment;

	public InterfaceElement(ScriptEnvironment environment, Stylesheet uniqueStylesheet, Stylesheet classStylesheet) {
		this.environment = environment;
		this.uniqueStylesheet = uniqueStylesheet;
		this.classStylesheet = classStylesheet;
	}

	public void addXAnchor(int addingAmount) {
		this.xAnchor += addingAmount;
	}

	public void addYAnchor(int addingAmount) {
		this.yAnchor += addingAmount;
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_InterfaceElement elem = new FauxTemplate_InterfaceElement(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING));
		elem.setElement(this);
		return elem;
	}

	public Color getBackgroundColor() {
		return ((StylesheetBackgroundColorElement) this.getStyleElement(StylesheetElementType.BACKGROUNDCOLOR)).getColor();
	}

	public int getBottomBorderMagnitude() {
		return ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude();
	}

	public int getBottomFluffMagnitude() {
		return this.getBottomMarginMagnitude() + this.getBottomBorderMagnitude() + this.getBottomPaddingMagnitude();
	}

	public int getBottomMarginMagnitude() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
	}

	public int getBottomPaddingMagnitude() {
		return ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude();
	}

	public Stylesheet getClassStylesheet() {
		return this.classStylesheet;
	}

	public Font getCurrentFont() {
		return new Font(((StylesheetFontElement) this.getStyleElement(StylesheetElementType.FONTNAME)).getFontName(), ((StylesheetFontStyleElement) this.getStyleElement(StylesheetElementType.FONTSTYLE)).getStyle(), ((StylesheetFontSizeElement) this.getStyleElement(StylesheetElementType.FONTSIZE)).getFontSize());
	}

	public Color getCurrentTextColor() {
		return ((StylesheetColorElement) this.getStyleElement(StylesheetElementType.COLOR)).getColor();
	}

	@Override
	public Rectangle getDrawingBounds() {
		return new Rectangle(this.xAnchor, this.yAnchor, this.getInternalWidth() - 1, this.getInternalHeight() - 1);
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public int getFullHeight() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude() + ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude() + ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude() + this.getInternalHeight() + ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude() + ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude() + ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
	}

	public int getFullWidth() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude() + ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude() + ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude() + this.getInternalWidth() + ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude() + ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude() + ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
	}

	public int getHorizontalFluffMagnitude() {
		return this.getLeftFluffMagnitude() + this.getRightFluffMagnitude();
	}

	public int getInternalHeight() {
		StylesheetHeightElement element = (StylesheetHeightElement) this.getStyleElement(StylesheetElementType.HEIGHT);
		if (element instanceof StylesheetAbsoluteHeightElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * this.getParent().getContainerElement().getInternalHeight() - this.getVerticalFluffMagnitude());
		}
	}

	public int getInternalWidth() {
		StylesheetWidthElement element = (StylesheetWidthElement) this.getStyleElement(StylesheetElementType.WIDTH);
		if (element instanceof StylesheetAbsoluteWidthElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * this.getParent().getContainerElement().getInternalWidth() - this.getHorizontalFluffMagnitude());
		}
	}

	public int getLeftBorderMagnitude() {
		return ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude();
	}

	public int getLeftFluffMagnitude() {
		return this.getLeftMarginMagnitude() + this.getLeftBorderMagnitude() + this.getLeftPaddingMagnitude();
	}

	public int getLeftMarginMagnitude() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude();
	}

	public int getLeftPaddingMagnitude() {
		return ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude();
	}

	public StylesheetElement getNonRecursiveStyleElement(StylesheetElementType code) {
		StylesheetElement element = null;
		if (this.getUniqueStylesheet() != null) {
			element = this.getUniqueStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		if (this.getClassStylesheet() != null) {
			element = this.getClassStylesheet().getElement(code);
		}
		return element;
	}

	@Override
	public Interface_Container getParent() {
		return this.parent;
	}

	public int getRightBorderMagnitude() {
		return ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude();
	}

	public int getRightFluffMagnitude() {
		return this.getRightMarginMagnitude() + this.getRightBorderMagnitude() + this.getRightPaddingMagnitude();
	}

	public int getRightMarginMagnitude() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
	}

	public int getRightPaddingMagnitude() {
		return ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude();
	}

	public InterfaceElement_Root getRoot() {
		if (this.getParent() == null) {
			return null;
		}
		return this.getParent().getContainerElement().getRoot();
	}

	public StylesheetElement getStyleElement(StylesheetElementType code) {
		StylesheetElement element = null;
		if (this.getUniqueStylesheet() != null) {
			element = this.getUniqueStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		if (this.getClassStylesheet() != null) {
			element = this.getClassStylesheet().getElement(code);
			if (element != null) {
				return element;
			}
		}
		return this.getParent().getContainerElement().getStyleElement(code);
	}

	public int getTopBorderMagnitude() {
		return ((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude();
	}

	public int getTopFluffMagnitude() {
		return this.getTopMarginMagnitude() + this.getTopBorderMagnitude() + this.getTopPaddingMagnitude();
	}

	public int getTopMarginMagnitude() {
		return ((StylesheetMarginElement) this.getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude();
	}

	public int getTopPaddingMagnitude() {
		return ((StylesheetPaddingElement) this.getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude();
	}

	public Stylesheet getUniqueStylesheet() {
		return this.uniqueStylesheet;
	}

	public int getVerticalFluffMagnitude() {
		return this.getTopFluffMagnitude() + this.getBottomFluffMagnitude();
	}

	public int getXAnchor() {
		return this.xAnchor;
	}

	public int getYAnchor() {
		return this.yAnchor;
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Interface Element");
		assert Debugger.addSnapNode("Unique Stylesheet", this.uniqueStylesheet);
		assert Debugger.addSnapNode("Class Stylesheet", this.classStylesheet);
		assert Debugger.closeNode();
	}

	@Override
	public void paint(Graphics2D g2d) {
		if (!((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERLEFT)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERLEFT)).getColor());
			int xPos = this.getXAnchor() + this.getLeftMarginMagnitude();
			int yPos = this.getYAnchor() + this.getTopMarginMagnitude();
			int width = this.getLeftBorderMagnitude();
			int height = this.getTopBorderMagnitude() + this.getTopPaddingMagnitude() + this.getInternalHeight() + this.getBottomPaddingMagnitude() + this.getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERRIGHT)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERRIGHT)).getColor());
			int xPos = this.getXAnchor() + this.getLeftFluffMagnitude() + this.getInternalWidth() + this.getRightPaddingMagnitude();
			int yPos = this.getYAnchor() + this.getTopMarginMagnitude();
			int width = this.getRightBorderMagnitude();
			int height = this.getTopBorderMagnitude() + this.getTopPaddingMagnitude() + this.getInternalHeight() + this.getBottomPaddingMagnitude() + this.getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERTOP)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERTOP)).getColor());
			int xPos = this.getXAnchor() + this.getLeftMarginMagnitude();
			int yPos = this.getYAnchor() + this.getTopMarginMagnitude();
			int width = this.getLeftBorderMagnitude() + this.getLeftPaddingMagnitude() + this.getInternalWidth() + this.getRightPaddingMagnitude() + this.getRightBorderMagnitude();
			int height = this.getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		if (!((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERBOTTOM)).getStyle().equals(ScriptKeywordType.none)) {
			g2d.setColor(((StylesheetBorderElement) this.getStyleElement(StylesheetElementType.BORDERBOTTOM)).getColor());
			int xPos = this.getXAnchor() + this.getLeftMarginMagnitude();
			int yPos = this.getYAnchor() + this.getTopFluffMagnitude() + this.getInternalHeight() + this.getBottomPaddingMagnitude();
			int width = this.getLeftBorderMagnitude() + this.getLeftPaddingMagnitude() + this.getInternalWidth() + this.getRightPaddingMagnitude() + this.getRightBorderMagnitude();
			int height = this.getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos, yPos, width, height));
		}
		this.addXAnchor(this.getLeftFluffMagnitude());
		this.addYAnchor(this.getTopFluffMagnitude());
		g2d.setColor(this.getBackgroundColor());
		g2d.fill(new Rectangle(this.getXAnchor(), this.getYAnchor(), this.getInternalWidth(), this.getInternalHeight()));
		g2d.setFont(this.getCurrentFont());
		g2d.setColor(this.getCurrentTextColor());
	}

	public void setClassStylesheet(Stylesheet sheet) {
		this.classStylesheet = sheet;
	}

	@Override
	public void setParent(Interface_Container container) {
		this.parent = container;
	}

	@Override
	public void setPreferredWidth(int width) {
		assert Debugger.openNode("Setting Preferred Width (" + width + ")");
		assert Debugger.addSnapNode("Element", this);
		if (null == this.getUniqueStylesheet()) {
			this.uniqueStylesheet = new Stylesheet(this.environment);
			this.uniqueStylesheet.setUnique(true);
		}
		this.getUniqueStylesheet().addElement(StylesheetElementType.WIDTH, new StylesheetAbsoluteWidthElement(width));
		Debugger.closeNode();
	}

	public void setUniqueStylesheet(Stylesheet sheet) {
		this.uniqueStylesheet = sheet;
	}

	@Override
	public void setXAnchor(int xAnchor) {
		this.xAnchor = xAnchor;
	}

	@Override
	public void setYAnchor(int yAnchor) {
		this.yAnchor = yAnchor;
	}
}
