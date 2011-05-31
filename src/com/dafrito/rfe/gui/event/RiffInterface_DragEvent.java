/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_DragEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int xOffset, yOffset;
	private final double distance;

	public RiffInterface_DragEvent(int x, int y, MouseButton button, int xOffset, int yOffset, double distance) {
		super(x, y, button);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.distance = distance;
	}

	public double getDistance() {
		return this.distance;
	}

	public int getXOffset() {
		return this.xOffset;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Drag Events", "Mouse-Drag Event");
		super.nodificate();
		assert Debugger.addNode("X-Offset: " + this.getXOffset());
		assert Debugger.addNode("Y-Offset: " + this.getYOffset());
		assert Debugger.closeNode();
	}
}