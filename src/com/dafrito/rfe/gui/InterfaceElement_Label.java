package com.dafrito.rfe.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.dafrito.rfe.gui.style.Stylesheet;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;

public class InterfaceElement_Label extends InterfaceElement implements Nodeable {
	private String string;

	public InterfaceElement_Label(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle, String string) {
		super(env, uniqueStyle, classStyle);
		this.string = string;
	}

	@Override
	public Rectangle getDrawingBounds() {
		Graphics2D g2d = this.getRoot().getGraphics();
		Rectangle2D boundingRect = this.getCurrentFont().getStringBounds(this.string, g2d.getFontRenderContext());
		return new Rectangle((int) boundingRect.getWidth(), (int) boundingRect.getHeight());
	}

	@Override
	public int getInternalHeight() {
		return (int) this.getDrawingBounds().getHeight() / 2;
	}

	@Override
	public int getInternalWidth() {
		return (int) this.getDrawingBounds().getWidth();
	}

	public String getString() {
		return this.string;
	}

	@Override
	public void nodificate() {
		assert Logs.openNode("Label Interface Element");
		super.nodificate();
		assert Logs.addNode("Label: " + this.string);
		assert Logs.closeNode();
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Logs.openNode("Label Painting Operations", "Painting Label");
		assert Logs.addNode(this);
		super.paint(g2d);
		g2d.drawString(this.string, this.getXAnchor(), this.getYAnchor() + this.getInternalHeight());
		assert Logs.closeNode();
	}

	@Override
	public void setPreferredWidth(int width) {
	}

	public void setString(String string) {
		this.string = string;
	}
}
