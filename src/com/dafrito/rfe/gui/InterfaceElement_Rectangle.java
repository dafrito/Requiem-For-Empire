package com.dafrito.rfe.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.style.Stylesheet;

public class InterfaceElement_Rectangle extends InterfaceElement implements Nodeable {
	public InterfaceElement_Rectangle(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
		super(env, uniqueStyle, classStyle);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Rectangle Interface Element");
		super.nodificate();
		assert Debugger.closeNode();
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Debugger.openNode("Rectangle Painting Operations", "Painting Rectangle");
		assert Debugger.addNode(this);
		super.paint(g2d);
		g2d.fill(new Rectangle(this.getXAnchor(), this.getYAnchor(), this.getInternalWidth(), this.getInternalHeight()));
		assert Debugger.closeNode();
	}

	@Override
	public void setPreferredWidth(int width) {
	}
}
