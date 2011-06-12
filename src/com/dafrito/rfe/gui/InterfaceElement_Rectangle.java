package com.dafrito.rfe.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.gui.style.Stylesheet;
import com.dafrito.rfe.script.ScriptEnvironment;

public class InterfaceElement_Rectangle extends InterfaceElement {
	public InterfaceElement_Rectangle(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
		super(env, uniqueStyle, classStyle);
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
