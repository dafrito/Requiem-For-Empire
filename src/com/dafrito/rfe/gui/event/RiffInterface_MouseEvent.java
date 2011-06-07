/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffInterface_MouseEvent implements RiffInterface_Event {
	final int x, y;
	final MouseButton button;

	public RiffInterface_MouseEvent(int x, int y, MouseButton button) {
		this.x = x;
		this.y = y;
		this.button = button;
	}

	@Inspectable
	public MouseButton getButton() {
		return this.button;
	}

	@Inspectable
	public int getX() {
		return this.x;
	}

	@Inspectable
	public int getY() {
		return this.y;
	}

}