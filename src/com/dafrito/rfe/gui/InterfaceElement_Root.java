package com.dafrito.rfe.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.Exception_InternalError;
import com.dafrito.rfe.GraphicalElement;
import com.dafrito.rfe.ScriptEnvironment;
import com.dafrito.rfe.ScriptKeywordType;
import com.dafrito.rfe.gui.event.KeyEvent_KeyDown;
import com.dafrito.rfe.gui.event.KeyEvent_KeyUp;
import com.dafrito.rfe.gui.event.RiffInterface_ClickEvent;
import com.dafrito.rfe.gui.event.RiffInterface_DragEvent;
import com.dafrito.rfe.gui.event.RiffInterface_Event;
import com.dafrito.rfe.gui.event.RiffInterface_KeyListener;
import com.dafrito.rfe.gui.event.RiffInterface_MouseDownEvent;
import com.dafrito.rfe.gui.event.RiffInterface_MouseListener;
import com.dafrito.rfe.gui.event.RiffInterface_MouseUpEvent;
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
import com.dafrito.rfe.style.StylesheetPercentageHeightElement;
import com.dafrito.rfe.style.StylesheetPercentageWidthElement;
import com.dafrito.rfe.style.StylesheetWidthElement;

public class InterfaceElement_Root extends InterfaceElement implements Interface_Container {
	private final JPanel drawingPanel;
	private final List<RiffInterface_MouseListener> mouseListeners = new LinkedList<RiffInterface_MouseListener>();
	private GraphicalElement focusedElement;
	private final List<GraphicalElement> elements = new LinkedList<GraphicalElement>();

	public InterfaceElement_Root(ScriptEnvironment environment, JPanel drawingPanel) {
		super(environment, null, null);
		this.drawingPanel = drawingPanel;
	}

	@Override
	public void add(GraphicalElement element) {
		assert Debugger.openNode("Interface-Element Addition");
		assert Debugger.addSnapNode("This Element", this);
		assert Debugger.addSnapNode("Added Element", element);
		this.elements.add(element);
		element.setParent(this);
		assert Debugger.closeNode();
	}

	public void add(RiffInterface_MouseListener element) {
		if (element instanceof InterfaceElement) {
			this.add((InterfaceElement) element);
		}
		this.mouseListeners.add(element);
	}

	@Override
	public void clear() {
		this.elements.clear();
	}

	public void dispatchEvent(RiffInterface_Event rawEvent) {
		if (rawEvent instanceof KeyEvent_KeyUp) {
			KeyEvent_KeyUp event = (KeyEvent_KeyUp) rawEvent;
			if (this.focusedElement == null) {
				return;
			}
			if (this.focusedElement instanceof RiffInterface_KeyListener) {
				((RiffInterface_KeyListener) this.focusedElement).riffKeyEvent(event);
			}
		} else if (rawEvent instanceof KeyEvent_KeyDown) {
			KeyEvent_KeyDown event = (KeyEvent_KeyDown) rawEvent;
			if (this.focusedElement == null) {
				return;
			}
			if (this.focusedElement instanceof RiffInterface_KeyListener) {
				((RiffInterface_KeyListener) this.focusedElement).riffKeyEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_MouseDownEvent) {
			RiffInterface_MouseDownEvent event = (RiffInterface_MouseDownEvent) rawEvent;
			GraphicalElement element = this.getElement(event.getX(), event.getY());
			if (element == null) {
				this.focusedElement = null;
				return;
			}
			if (element.isFocusable()) {
				this.focusedElement = element;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_MouseUpEvent) {
			RiffInterface_MouseUpEvent event = (RiffInterface_MouseUpEvent) rawEvent;
			GraphicalElement element = this.getElement(event.getX(), event.getY());
			if (element == null) {
				return;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_ClickEvent) {
			RiffInterface_ClickEvent event = (RiffInterface_ClickEvent) rawEvent;
			GraphicalElement element = this.getElement(event.getX(), event.getY());
			if (element == null) {
				this.focusedElement = null;
				return;
			}
			if (element.isFocusable()) {
				this.focusedElement = element;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_DragEvent) {
			RiffInterface_DragEvent event = (RiffInterface_DragEvent) rawEvent;
			if (this.focusedElement == null) {
				return;
			}
			if (this.focusedElement instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) this.focusedElement).riffMouseEvent(event);
			}
		} else {
			assert Debugger.addNode("No applicable listener found");
		}
	}

	@Override
	public InterfaceElement getContainerElement() {
		return this;
	}

	public GraphicalElement getElement(int x, int y) {
		for (GraphicalElement element : this.getElements()) {
			if (element.getDrawingBounds().contains(x, y)) {
				return element;
			}
		}
		return null;
	}

	@Override
	public java.util.List<GraphicalElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}

	public Graphics2D getGraphics() {
		return (Graphics2D) this.drawingPanel.getGraphics();
	}

	@Override
	public int getInternalHeight() {
		StylesheetHeightElement element = (StylesheetHeightElement) this.getStyleElement(StylesheetElementType.HEIGHT);
		if (element instanceof StylesheetAbsoluteHeightElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * this.drawingPanel.getHeight() - this.getVerticalFluffMagnitude());
		}
	}

	@Override
	public int getInternalWidth() {
		StylesheetWidthElement element = (StylesheetWidthElement) this.getStyleElement(StylesheetElementType.WIDTH);
		if (element instanceof StylesheetAbsoluteWidthElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * this.drawingPanel.getWidth() - this.getHorizontalFluffMagnitude());
		}
	}

	@Override
	public InterfaceElement_Root getRoot() {
		return this;
	}

	@Override
	public StylesheetElement getStyleElement(StylesheetElementType code) {
		Stylesheet sheet = this.getUniqueStylesheet();
		if (sheet == null) {
			this.setUniqueStylesheet(new Stylesheet(this.getEnvironment()));
			sheet = this.getUniqueStylesheet();
			sheet.setUnique(true);
		}
		StylesheetElement element = this.getUniqueStylesheet().getElement(code);
		if (code == StylesheetElementType.WIDTH) {
			if (element != null) {
				return element;
			}
			element = new StylesheetPercentageWidthElement(1);
			sheet.addElement(code, element);
			return element;
		} else if (code == StylesheetElementType.HEIGHT) {
			if (element != null) {
				return element;
			}
			element = new StylesheetPercentageHeightElement(1);
			sheet.addElement(code, element);
			return element;
		} else if (code == StylesheetElementType.COLOR) {
			if (element != null) {
				return element;
			}
			element = new StylesheetColorElement("white");
			sheet.addElement(code, element);
			return element;
		} else if (code == StylesheetElementType.FONTNAME) {
			if (element != null) {
				return element;
			}
			element = new StylesheetFontElement("Lucida Sans Unicode");
			sheet.addElement(code, element);
			return element;
		} else if (code == StylesheetElementType.FONTSIZE) {
			if (element != null) {
				return element;
			}
			element = new StylesheetFontSizeElement(18);
			sheet.addElement(code, element);
			return element;
		} else if (code == StylesheetElementType.FONTSTYLE) {
			if (element != null) {
				return element;
			}
			element = new StylesheetFontStyleElement(Font.PLAIN);
			sheet.addElement(code, element);
			return element;
			// Borders
		} else if (code == StylesheetElementType.BORDERBOTTOM || code == StylesheetElementType.BORDERTOP || code == StylesheetElementType.BORDERLEFT || code == StylesheetElementType.BORDERRIGHT) {
			if (element != null) {
				return element;
			}
			element = new StylesheetBorderElement(0, ScriptKeywordType.solid, Color.BLACK);
			sheet.addElement(code, element);
			return element;
			// Margins
		} else if (code == StylesheetElementType.MARGINLEFT || code == StylesheetElementType.MARGINRIGHT || code == StylesheetElementType.MARGINTOP || code == StylesheetElementType.MARGINBOTTOM) {
			if (element != null) {
				return element;
			}
			element = new StylesheetMarginElement(0);
			sheet.addElement(code, element);
			return element;
			// Padding
		} else if (code == StylesheetElementType.PADDINGLEFT || code == StylesheetElementType.PADDINGRIGHT || code == StylesheetElementType.PADDINGTOP || code == StylesheetElementType.PADDINGBOTTOM) {
			if (element != null) {
				return element;
			}
			element = new StylesheetPaddingElement(0);
			sheet.addElement(code, element);
			return element;
			// Background color
		} else if (code == StylesheetElementType.BACKGROUNDCOLOR) {
			if (element != null) {
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
	public void nodificate() {
		assert Debugger.openNode("Root Interface Element");
		super.nodificate();
		assert Debugger.addSnapNode("Elements: " + this.elements.size() + " element(s)", this.elements);
		assert Debugger.closeNode();
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Debugger.openNode("Paint Operations", "Painting operation: " + this.getElements().size() + " element(s)");
		this.setXAnchor(0);
		this.setYAnchor(0);
		super.paint(g2d);
		int xAnchorOffset = this.getXAnchor();
		int yAnchorOffset = this.getYAnchor();
		int nextLineAnchorOffset = 0;
		int width = this.getInternalWidth();
		for (GraphicalElement element : this.getElements()) {
			Rectangle rect = element.getDrawingBounds();
			if (rect.getWidth() > width - xAnchorOffset) {
				element.setPreferredWidth(width - xAnchorOffset);
				rect = element.getDrawingBounds();
				if (rect.getWidth() > width - xAnchorOffset) {
					yAnchorOffset += nextLineAnchorOffset;
					xAnchorOffset = this.getXAnchor();
					nextLineAnchorOffset = 0;
				}
			}
			element.setXAnchor(xAnchorOffset);
			element.setYAnchor(yAnchorOffset);
			xAnchorOffset += rect.getWidth();
			if (nextLineAnchorOffset < rect.getHeight()) {
				nextLineAnchorOffset = (int) rect.getHeight();
			}
			element.paint(g2d);
		}
		assert Debugger.closeNode();
	}

	public void repaint() {
		this.drawingPanel.repaint();
	}
}
