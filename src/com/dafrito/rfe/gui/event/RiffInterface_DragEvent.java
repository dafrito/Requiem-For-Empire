/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffInterface_DragEvent extends RiffInterface_MouseEvent {
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

	@Inspectable
	public int getXOffset() {
		return this.xOffset;
	}

	@Inspectable
	public int getYOffset() {
		return this.yOffset;
	}
}