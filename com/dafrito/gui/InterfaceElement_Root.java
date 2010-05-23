package com.dafrito.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
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
import com.dafrito.gui.style.Elements.StylesheetPercentageHeightElement;
import com.dafrito.gui.style.Elements.StylesheetPercentageWidthElement;
import com.dafrito.gui.style.Elements.StylesheetWidthElement;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;

public class InterfaceElement_Root extends InterfaceElement implements Interface_Container {
    private JPanel drawingPanel;
    private static int drawingIteration = 0;
    private java.util.List<RiffInterface_MouseListener> mouseListeners;
    private GraphicalElement focusedElement;
    private java.util.List<GraphicalElement> elements = new LinkedList<GraphicalElement>();

    public InterfaceElement_Root(ScriptEnvironment environment, JPanel drawingPanel) {
        super(environment, null, null);
        this.mouseListeners = new LinkedList<RiffInterface_MouseListener>();
        this.drawingPanel = drawingPanel;
    }

    public void clear() {
        this.elements.clear();
    }

    public void add(GraphicalElement element) {
        assert LegacyDebugger.open("Interface-Element Addition");
        assert LegacyDebugger.addSnapNode("This Element", this);
        assert LegacyDebugger.addSnapNode("Added Element", element);
        this.elements.add(element);
        element.setParent(this);
        assert LegacyDebugger.close();
    }

    public InterfaceElement getContainerElement() {
        return this;
    }

    public java.util.List<GraphicalElement> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    public void repaint() {
        this.drawingPanel.repaint();
    }

    public void add(RiffInterface_MouseListener element) {
        if(element instanceof InterfaceElement) {
            add((InterfaceElement)element);
        }
        this.mouseListeners.add(element);
    }

    public void dispatchEvent(RiffInterface_Event rawEvent) {
        if(rawEvent instanceof KeyEvent_KeyUp) {
            KeyEvent_KeyUp event = (KeyEvent_KeyUp)rawEvent;
            if(this.focusedElement == null) {
                return;
            }
            if(this.focusedElement instanceof RiffInterface_KeyListener) {
                ((RiffInterface_KeyListener)this.focusedElement).riffKeyEvent(event);
            }
        } else if(rawEvent instanceof KeyEvent_KeyDown) {
            KeyEvent_KeyDown event = (KeyEvent_KeyDown)rawEvent;
            if(this.focusedElement == null) {
                return;
            }
            if(this.focusedElement instanceof RiffInterface_KeyListener) {
                ((RiffInterface_KeyListener)this.focusedElement).riffKeyEvent(event);
            }
        } else if(rawEvent instanceof RiffInterface_MouseDownEvent) {
            RiffInterface_MouseDownEvent event = (RiffInterface_MouseDownEvent)rawEvent;
            GraphicalElement element = getElement(event.getX(), event.getY());
            if(element == null) {
                this.focusedElement = null;
                return;
            }
            if(element.isFocusable()) {
                this.focusedElement = element;
            }
            if(element instanceof RiffInterface_MouseListener) {
                ((RiffInterface_MouseListener)element).riffMouseEvent(event);
            }
        } else if(rawEvent instanceof RiffInterface_MouseUpEvent) {
            RiffInterface_MouseUpEvent event = (RiffInterface_MouseUpEvent)rawEvent;
            GraphicalElement element = getElement(event.getX(), event.getY());
            if(element == null) {
                return;
            }
            if(element instanceof RiffInterface_MouseListener) {
                ((RiffInterface_MouseListener)element).riffMouseEvent(event);
            }
        } else if(rawEvent instanceof RiffInterface_ClickEvent) {
            RiffInterface_ClickEvent event = (RiffInterface_ClickEvent)rawEvent;
            GraphicalElement element = getElement(event.getX(), event.getY());
            if(element == null) {
                this.focusedElement = null;
                return;
            }
            if(element.isFocusable()) {
                this.focusedElement = element;
            }
            if(element instanceof RiffInterface_MouseListener) {
                ((RiffInterface_MouseListener)element).riffMouseEvent(event);
            }
        } else if(rawEvent instanceof RiffInterface_DragEvent) {
            RiffInterface_DragEvent event = (RiffInterface_DragEvent)rawEvent;
            if(this.focusedElement == null) {
                return;
            }
            if(this.focusedElement instanceof RiffInterface_MouseListener) {
                ((RiffInterface_MouseListener)this.focusedElement).riffMouseEvent(event);
            }
        } else {
            assert LegacyDebugger.addNode("No applicable listener found");
        }
    }

    public GraphicalElement getElement(int x, int y) {
        for(GraphicalElement element : getElements()) {
            if(element.getDrawingBounds().contains(x, y)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public int getInternalWidth() {
        StylesheetWidthElement element = (StylesheetWidthElement)getStyleElement(StylesheetElementType.WIDTH);
        if(element instanceof StylesheetAbsoluteWidthElement) {
            return ((Integer)element.getMagnitude()).intValue();
        }
        return (int)(((Double)element.getMagnitude()).doubleValue() * this.drawingPanel.getWidth() - getHorizontalFluffMagnitude());
    }

    @Override
    public int getInternalHeight() {
        StylesheetHeightElement element = (StylesheetHeightElement)getStyleElement(StylesheetElementType.HEIGHT);
        if(element instanceof StylesheetAbsoluteHeightElement) {
            return ((Integer)element.getMagnitude()).intValue();
        }
        return (int)(((Double)element.getMagnitude()).doubleValue() * this.drawingPanel.getHeight() - getVerticalFluffMagnitude());
    }

    @Override
    public InterfaceElement_Root getRoot() {
        return this;
    }

    public Graphics2D getGraphics() {
        return (Graphics2D)this.drawingPanel.getGraphics();
    }

    @Override
    public void paint(Graphics2D g2d) {
        assert LegacyDebugger.open("Paint Operations", "Painting operation: " + getElements().size() + " element(s)");
        drawingIteration++;
        setXAnchor(0);
        setYAnchor(0);
        super.paint(g2d);
        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, 200, 200);
        int xAnchorOffset = getXAnchor();
        int yAnchorOffset = getYAnchor();
        int nextLineAnchorOffset = 0;
        int width = getInternalWidth();
        for(GraphicalElement element : getElements()) {
            Rectangle rect = element.getDrawingBounds();
            if(rect.getWidth() > width - xAnchorOffset) {
                element.setPreferredWidth(width - xAnchorOffset);
                rect = element.getDrawingBounds();
                if(rect.getWidth() > width - xAnchorOffset) {
                    yAnchorOffset += nextLineAnchorOffset;
                    xAnchorOffset = getXAnchor();
                    nextLineAnchorOffset = 0;
                }
            }
            element.setXAnchor(xAnchorOffset);
            element.setYAnchor(yAnchorOffset);
            xAnchorOffset += rect.getWidth();
            if(nextLineAnchorOffset < rect.getHeight()) {
                nextLineAnchorOffset = (int)rect.getHeight();
            }
            element.paint(g2d);
        }
        assert LegacyDebugger.close();
    }

    @Override
    public StylesheetElement getStyleElement(StylesheetElementType code) {
        Stylesheet sheet = getUniqueStylesheet();
        if(sheet == null) {
            setUniqueStylesheet(new Stylesheet(getEnvironment()));
            sheet = getUniqueStylesheet();
            sheet.setUnique(true);
        }
        StylesheetElement element = getUniqueStylesheet().getElement(code);
        if(code == StylesheetElementType.WIDTH) {
            if(element != null) {
                return element;
            }
            element = new StylesheetPercentageWidthElement(1);
            sheet.addElement(code, element);
            return element;
        } else if(code == StylesheetElementType.HEIGHT) {
            if(element != null) {
                return element;
            }
            element = new StylesheetPercentageHeightElement(1);
            sheet.addElement(code, element);
            return element;
        } else if(code == StylesheetElementType.COLOR) {
            if(element != null) {
                return element;
            }
            element = new StylesheetColorElement("white");
            sheet.addElement(code, element);
            return element;
        } else if(code == StylesheetElementType.FONTNAME) {
            if(element != null) {
                return element;
            }
            element = new StylesheetFontElement("Lucida Sans Unicode");
            sheet.addElement(code, element);
            return element;
        } else if(code == StylesheetElementType.FONTSIZE) {
            if(element != null) {
                return element;
            }
            element = new StylesheetFontSizeElement(18);
            sheet.addElement(code, element);
            return element;
        } else if(code == StylesheetElementType.FONTSTYLE) {
            if(element != null) {
                return element;
            }
            element = new StylesheetFontStyleElement(Font.PLAIN);
            sheet.addElement(code, element);
            return element;
            // Borders
        } else if(code == StylesheetElementType.BORDERBOTTOM
            || code == StylesheetElementType.BORDERTOP
            || code == StylesheetElementType.BORDERLEFT
            || code == StylesheetElementType.BORDERRIGHT) {
            if(element != null) {
                return element;
            }
            element = new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
            sheet.addElement(code, element);
            return element;
            // Margins
        } else if(code == StylesheetElementType.MARGINLEFT
            || code == StylesheetElementType.MARGINRIGHT
            || code == StylesheetElementType.MARGINTOP
            || code == StylesheetElementType.MARGINBOTTOM) {
            if(element != null) {
                return element;
            }
            element = new StylesheetMarginElement(0);
            sheet.addElement(code, element);
            return element;
            // Padding
        } else if(code == StylesheetElementType.PADDINGLEFT
            || code == StylesheetElementType.PADDINGRIGHT
            || code == StylesheetElementType.PADDINGTOP
            || code == StylesheetElementType.PADDINGBOTTOM) {
            if(element != null) {
                return element;
            }
            element = new StylesheetPaddingElement(0);
            sheet.addElement(code, element);
            return element;
            // Background color
        } else if(code == StylesheetElementType.BACKGROUNDCOLOR) {
            if(element != null) {
                return element;
            }
            element = new StylesheetBackgroundColorElement(Color.BLACK);
            sheet.addElement(code, element);
            return element;
        } else {
            throw new Exception_InternalError("No default setting set for element: " + code);
        }
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Root Interface Element");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Elements: " + this.elements.size() + " element(s)", this.elements);
        assert LegacyDebugger.close();
        return true;
    }
}
