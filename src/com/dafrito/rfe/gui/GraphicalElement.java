package com.dafrito.rfe.gui;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.rfe.script.ScriptEnvironment;

public interface GraphicalElement {
	public Rectangle getDrawingBounds();

	public ScriptEnvironment getEnvironment();

	public Interface_Container getParent();

	public boolean isFocusable();

	public void paint(Graphics2D g2d);

	public void setParent(Interface_Container container);

	public void setPreferredWidth(int width);

	public void setXAnchor(int x);

	public void setYAnchor(int y);
}
