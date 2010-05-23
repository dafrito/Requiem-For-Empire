package com.dafrito.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.gui.style.StylesheetElement;
import com.dafrito.gui.style.StylesheetElementType;
import com.dafrito.gui.style.Elements.StylesheetAbsoluteHeightElement;
import com.dafrito.gui.style.Elements.StylesheetAbsoluteWidthElement;
import com.dafrito.gui.style.Elements.StylesheetBackgroundColorElement;
import com.dafrito.gui.style.Elements.StylesheetBorderElement;
import com.dafrito.gui.style.Elements.StylesheetColorElement;
import com.dafrito.gui.style.Elements.StylesheetFontElement;
import com.dafrito.gui.style.Elements.StylesheetFontSizeElement;
import com.dafrito.gui.style.Elements.StylesheetFontStyleElement;
import com.dafrito.gui.style.Elements.StylesheetHeightElement;
import com.dafrito.gui.style.Elements.StylesheetMarginElement;
import com.dafrito.gui.style.Elements.StylesheetPaddingElement;
import com.dafrito.gui.style.Elements.StylesheetWidthElement;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.templates.FauxTemplate_InterfaceElement;
import com.dafrito.script.types.ScriptValueType;

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

    public Rectangle getDrawingBounds() {
        return new Rectangle(this.xAnchor, this.yAnchor, getInternalWidth() - 1, getInternalHeight() - 1);
    }

    public boolean isFocusable() {
        return false;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public void setPreferredWidth(int width) {
        assert LegacyDebugger.open("Setting Preferred Width (" + width + ")");
        assert LegacyDebugger.addSnapNode("Element", this);
        if(null == getUniqueStylesheet()) {
            this.uniqueStylesheet = new Stylesheet(this.environment);
            this.uniqueStylesheet.setUnique(true);
        }
        getUniqueStylesheet().addElement(StylesheetElementType.WIDTH, new StylesheetAbsoluteWidthElement(width));
        LegacyDebugger.close();
    }

    public Font getCurrentFont() {
        return new Font(((StylesheetFontElement)getStyleElement(StylesheetElementType.FONTNAME)).getFontName(), ((StylesheetFontStyleElement)getStyleElement(StylesheetElementType.FONTSTYLE)).getStyle(), ((StylesheetFontSizeElement)getStyleElement(StylesheetElementType.FONTSIZE)).getFontSize());
    }

    public Color getCurrentTextColor() {
        return ((StylesheetColorElement)getStyleElement(StylesheetElementType.COLOR)).getColor();
    }

    public Stylesheet getUniqueStylesheet() {
        return this.uniqueStylesheet;
    }

    public Stylesheet getClassStylesheet() {
        return this.classStylesheet;
    }

    public void setUniqueStylesheet(Stylesheet sheet) {
        this.uniqueStylesheet = sheet;
    }

    public void setClassStylesheet(Stylesheet sheet) {
        this.classStylesheet = sheet;
    }

    public StylesheetElement getStyleElement(StylesheetElementType code) {
        StylesheetElement element = null;
        if(getUniqueStylesheet() != null) {
            element = getUniqueStylesheet().getElement(code);
            if(element != null) {
                return element;
            }
        }
        if(getClassStylesheet() != null) {
            element = getClassStylesheet().getElement(code);
            if(element != null) {
                return element;
            }
        }
        return getParent().getContainerElement().getStyleElement(code);
    }

    public int getLeftMarginMagnitude() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude();
    }

    public int getRightMarginMagnitude() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
    }

    public int getBottomMarginMagnitude() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
    }

    public int getTopMarginMagnitude() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude();
    }

    public int getLeftPaddingMagnitude() {
        return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude();
    }

    public int getRightPaddingMagnitude() {
        return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude();
    }

    public int getBottomPaddingMagnitude() {
        return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude();
    }

    public int getTopPaddingMagnitude() {
        return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude();
    }

    public int getLeftBorderMagnitude() {
        return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude();
    }

    public int getRightBorderMagnitude() {
        return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude();
    }

    public int getBottomBorderMagnitude() {
        return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude();
    }

    public int getTopBorderMagnitude() {
        return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude();
    }

    public Color getBackgroundColor() {
        return ((StylesheetBackgroundColorElement)getStyleElement(StylesheetElementType.BACKGROUNDCOLOR)).getColor();
    }

    public StylesheetElement getNonRecursiveStyleElement(StylesheetElementType code) {
        StylesheetElement element = null;
        if(getUniqueStylesheet() != null) {
            element = getUniqueStylesheet().getElement(code);
            if(element != null) {
                return element;
            }
        }
        if(getClassStylesheet() != null) {
            element = getClassStylesheet().getElement(code);
        }
        return element;
    }

    public int getLeftFluffMagnitude() {
        return getLeftMarginMagnitude() + getLeftBorderMagnitude() + getLeftPaddingMagnitude();
    }

    public int getRightFluffMagnitude() {
        return getRightMarginMagnitude() + getRightBorderMagnitude() + getRightPaddingMagnitude();
    }

    public int getHorizontalFluffMagnitude() {
        return getLeftFluffMagnitude() + getRightFluffMagnitude();
    }

    public int getTopFluffMagnitude() {
        return getTopMarginMagnitude() + getTopBorderMagnitude() + getTopPaddingMagnitude();
    }

    public int getBottomFluffMagnitude() {
        return getBottomMarginMagnitude() + getBottomBorderMagnitude() + getBottomPaddingMagnitude();
    }

    public int getVerticalFluffMagnitude() {
        return getTopFluffMagnitude() + getBottomFluffMagnitude();
    }

    public int getInternalWidth() {
        StylesheetWidthElement element = (StylesheetWidthElement)getStyleElement(StylesheetElementType.WIDTH);
        if(element instanceof StylesheetAbsoluteWidthElement) {
            return ((Integer)element.getMagnitude()).intValue();
        }
        return (int)(((Double)element.getMagnitude()).doubleValue()
            * getParent().getContainerElement().getInternalWidth() - getHorizontalFluffMagnitude());
    }

    public int getInternalHeight() {
        StylesheetHeightElement element = (StylesheetHeightElement)getStyleElement(StylesheetElementType.HEIGHT);
        if(element instanceof StylesheetAbsoluteHeightElement) {
            return ((Integer)element.getMagnitude()).intValue();
        }
        return (int)(((Double)element.getMagnitude()).doubleValue()
            * getParent().getContainerElement().getInternalHeight() - getVerticalFluffMagnitude());
    }

    public int getFullWidth() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude()
            + ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude()
            + ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude()
            + getInternalWidth()
            + ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude()
            + ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude()
            + ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
    }

    public int getFullHeight() {
        return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude()
            + ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude()
            + ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude()
            + getInternalHeight()
            + ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude()
            + ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude()
            + ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
    }

    public void setXAnchor(int xAnchor) {
        this.xAnchor = xAnchor;
    }

    public void setYAnchor(int yAnchor) {
        this.yAnchor = yAnchor;
    }

    public int getXAnchor() {
        return this.xAnchor;
    }

    public int getYAnchor() {
        return this.yAnchor;
    }

    public void addXAnchor(int addingAmount) {
        this.xAnchor += addingAmount;
    }

    public void addYAnchor(int addingAmount) {
        this.yAnchor += addingAmount;
    }

    public InterfaceElement_Root getRoot() {
        if(getParent() == null) {
            return null;
        }
        return getParent().getContainerElement().getRoot();
    }

    public Interface_Container getParent() {
        return this.parent;
    }

    public void setParent(Interface_Container container) {
        this.parent = container;
    }

    public void paint(Graphics2D g2d) {
        if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getStyle().equals(
            ScriptKeywordType.none)) {
            g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getColor());
            int xPos = getXAnchor() + getLeftMarginMagnitude();
            int yPos = getYAnchor() + getTopMarginMagnitude();
            int width = getLeftBorderMagnitude();
            int height = getTopBorderMagnitude()
                + getTopPaddingMagnitude()
                + getInternalHeight()
                + getBottomPaddingMagnitude()
                + getBottomBorderMagnitude();
            g2d.fill(new Rectangle(xPos, yPos, width, height));
        }
        if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getStyle().equals(
            ScriptKeywordType.none)) {
            g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getColor());
            int xPos = getXAnchor() + getLeftFluffMagnitude() + getInternalWidth() + getRightPaddingMagnitude();
            int yPos = getYAnchor() + getTopMarginMagnitude();
            int width = getRightBorderMagnitude();
            int height = getTopBorderMagnitude()
                + getTopPaddingMagnitude()
                + getInternalHeight()
                + getBottomPaddingMagnitude()
                + getBottomBorderMagnitude();
            g2d.fill(new Rectangle(xPos, yPos, width, height));
        }
        if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getStyle().equals(
            ScriptKeywordType.none)) {
            g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getColor());
            int xPos = getXAnchor() + getLeftMarginMagnitude();
            int yPos = getYAnchor() + getTopMarginMagnitude();
            int width = getLeftBorderMagnitude()
                + getLeftPaddingMagnitude()
                + getInternalWidth()
                + getRightPaddingMagnitude()
                + getRightBorderMagnitude();
            int height = getLeftBorderMagnitude();
            g2d.fill(new Rectangle(xPos, yPos, width, height));
        }
        if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getStyle().equals(
            ScriptKeywordType.none)) {
            g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getColor());
            int xPos = getXAnchor() + getLeftMarginMagnitude();
            int yPos = getYAnchor() + getTopFluffMagnitude() + getInternalHeight() + getBottomPaddingMagnitude();
            int width = getLeftBorderMagnitude()
                + getLeftPaddingMagnitude()
                + getInternalWidth()
                + getRightPaddingMagnitude()
                + getRightBorderMagnitude();
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

    // ScriptConvertible implementation
    public Object convert() {
        FauxTemplate_InterfaceElement elem = new FauxTemplate_InterfaceElement(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING));
        elem.setElement(this);
        return elem;
    }

    // Nodeable implementation
    public boolean nodificate() {
        assert LegacyDebugger.open("Interface Element");
        assert LegacyDebugger.addSnapNode("Unique Stylesheet", this.uniqueStylesheet);
        assert LegacyDebugger.addSnapNode("Class Stylesheet", this.classStylesheet);
        assert LegacyDebugger.close();
        return true;
    }
}
