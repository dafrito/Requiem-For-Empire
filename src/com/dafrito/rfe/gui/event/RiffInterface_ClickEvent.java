/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.MouseButton;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_ClickEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int clicks;

	public RiffInterface_ClickEvent(int x, int y, MouseButton button, int clicks) {
		super(x, y, button);
		this.clicks = clicks;
	}

	public int getClicks() {
		return this.clicks;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Click Events", "Click Event");
		super.nodificate();
		assert Debugger.addNode("Clicks: " + this.clicks);
		assert Debugger.closeNode();
	}
}