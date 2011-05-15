import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JPanel;

public class InterfaceElement_Root extends InterfaceElement implements Interface_Container {
	private JPanel m_drawingPanel;
	private static int m_drawingIteration = 0;
	private java.util.List<RiffInterface_MouseListener> m_mouseListeners;
	private GraphicalElement m_focusedElement;
	private java.util.List<GraphicalElement> m_elements = new LinkedList<GraphicalElement>();

	public InterfaceElement_Root(ScriptEnvironment environment, JPanel drawingPanel) {
		super(environment, null, null);
		m_mouseListeners = new LinkedList<RiffInterface_MouseListener>();
		m_drawingPanel = drawingPanel;
	}

	@Override
	public void add(GraphicalElement element) {
		assert Debugger.openNode("Interface-Element Addition");
		assert Debugger.addSnapNode("This Element", this);
		assert Debugger.addSnapNode("Added Element", element);
		m_elements.add(element);
		element.setParent(this);
		assert Debugger.closeNode();
	}

	public void add(RiffInterface_MouseListener element) {
		if (element instanceof InterfaceElement) {
			add((InterfaceElement) element);
		}
		m_mouseListeners.add(element);
	}

	@Override
	public void clear() {
		m_elements.clear();
	}

	public void dispatchEvent(RiffInterface_Event rawEvent) {
		if (rawEvent instanceof KeyEvent_KeyUp) {
			KeyEvent_KeyUp event = (KeyEvent_KeyUp) rawEvent;
			if (m_focusedElement == null) {
				return;
			}
			if (m_focusedElement instanceof RiffInterface_KeyListener) {
				((RiffInterface_KeyListener) m_focusedElement).riffKeyEvent(event);
			}
		} else if (rawEvent instanceof KeyEvent_KeyDown) {
			KeyEvent_KeyDown event = (KeyEvent_KeyDown) rawEvent;
			if (m_focusedElement == null) {
				return;
			}
			if (m_focusedElement instanceof RiffInterface_KeyListener) {
				((RiffInterface_KeyListener) m_focusedElement).riffKeyEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_MouseDownEvent) {
			RiffInterface_MouseDownEvent event = (RiffInterface_MouseDownEvent) rawEvent;
			GraphicalElement element = getElement(event.getX(), event.getY());
			if (element == null) {
				m_focusedElement = null;
				return;
			}
			if (element.isFocusable()) {
				m_focusedElement = element;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_MouseUpEvent) {
			RiffInterface_MouseUpEvent event = (RiffInterface_MouseUpEvent) rawEvent;
			GraphicalElement element = getElement(event.getX(), event.getY());
			if (element == null) {
				return;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_ClickEvent) {
			RiffInterface_ClickEvent event = (RiffInterface_ClickEvent) rawEvent;
			GraphicalElement element = getElement(event.getX(), event.getY());
			if (element == null) {
				m_focusedElement = null;
				return;
			}
			if (element.isFocusable()) {
				m_focusedElement = element;
			}
			if (element instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) element).riffMouseEvent(event);
			}
		} else if (rawEvent instanceof RiffInterface_DragEvent) {
			RiffInterface_DragEvent event = (RiffInterface_DragEvent) rawEvent;
			if (m_focusedElement == null) {
				return;
			}
			if (m_focusedElement instanceof RiffInterface_MouseListener) {
				((RiffInterface_MouseListener) m_focusedElement).riffMouseEvent(event);
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
		for (GraphicalElement element : getElements()) {
			if (element.getDrawingBounds().contains(x, y)) {
				return element;
			}
		}
		return null;
	}

	@Override
	public java.util.List<GraphicalElement> getElements() {
		return Collections.unmodifiableList(m_elements);
	}

	public Graphics2D getGraphics() {
		return (Graphics2D) m_drawingPanel.getGraphics();
	}

	@Override
	public int getInternalHeight() {
		StylesheetHeightElement element = (StylesheetHeightElement) getStyleElement(StylesheetElementType.HEIGHT);
		if (element instanceof StylesheetAbsoluteHeightElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * m_drawingPanel.getHeight() - getVerticalFluffMagnitude());
		}
	}

	@Override
	public int getInternalWidth() {
		StylesheetWidthElement element = (StylesheetWidthElement) getStyleElement(StylesheetElementType.WIDTH);
		if (element instanceof StylesheetAbsoluteWidthElement) {
			return ((Integer) element.getMagnitude()).intValue();
		} else {
			return (int) (((Double) element.getMagnitude()).doubleValue() * m_drawingPanel.getWidth() - getHorizontalFluffMagnitude());
		}
	}

	@Override
	public InterfaceElement_Root getRoot() {
		return this;
	}

	@Override
	public StylesheetElement getStyleElement(StylesheetElementType code) {
		Stylesheet sheet = getUniqueStylesheet();
		if (sheet == null) {
			setUniqueStylesheet(new Stylesheet(getEnvironment()));
			sheet = getUniqueStylesheet();
			sheet.setUnique(true);
		}
		StylesheetElement element = getUniqueStylesheet().getElement(code);
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
	public boolean nodificate() {
		assert Debugger.openNode("Root Interface Element");
		assert super.nodificate();
		assert Debugger.addSnapNode("Elements: " + m_elements.size() + " element(s)", m_elements);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Debugger.openNode("Paint Operations", "Painting operation: " + getElements().size() + " element(s)");
		m_drawingIteration++;
		setXAnchor(0);
		setYAnchor(0);
		super.paint(g2d);
		int xAnchorOffset = getXAnchor();
		int yAnchorOffset = getYAnchor();
		int nextLineAnchorOffset = 0;
		int width = getInternalWidth();
		for (GraphicalElement element : getElements()) {
			Rectangle rect = element.getDrawingBounds();
			if (rect.getWidth() > width - xAnchorOffset) {
				element.setPreferredWidth(width - xAnchorOffset);
				rect = element.getDrawingBounds();
				if (rect.getWidth() > width - xAnchorOffset) {
					yAnchorOffset += nextLineAnchorOffset;
					xAnchorOffset = getXAnchor();
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
		m_drawingPanel.repaint();
	}
}
