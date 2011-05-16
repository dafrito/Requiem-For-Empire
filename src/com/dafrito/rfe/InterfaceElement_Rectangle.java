package com.dafrito.rfe;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public class InterfaceElement_Rectangle extends InterfaceElement implements Nodeable {
	public InterfaceElement_Rectangle(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
		super(env, uniqueStyle, classStyle);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Rectangle Interface Element");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
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
