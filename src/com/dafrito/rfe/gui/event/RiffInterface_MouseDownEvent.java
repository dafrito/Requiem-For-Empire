/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.MouseButton;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseDownEvent(int x, int y, MouseButton button) {
		super(x, y, button);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Down Events", "Mouse-Down Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}