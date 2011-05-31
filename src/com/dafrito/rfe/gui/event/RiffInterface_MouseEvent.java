/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_MouseEvent implements Nodeable, RiffInterface_Event {
	final int x, y;
	final MouseButton button;

	public RiffInterface_MouseEvent(int x, int y, MouseButton button) {
		this.x = x;
		this.y = y;
		this.button = button;
	}

	public MouseButton getButton() {
		return this.button;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Generic Mouse Event");
		assert Debugger.addNode("X: " + this.getX());
		assert Debugger.addNode("Y: " + this.getY());
		assert Debugger.addNode("Button: " + this.getButton());
		assert Debugger.closeNode();
	}
}