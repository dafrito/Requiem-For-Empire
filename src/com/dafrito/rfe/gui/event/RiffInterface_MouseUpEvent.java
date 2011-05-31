/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_MouseUpEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseUpEvent(int x, int y, MouseButton button) {
		super(x, y, button);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Up Events", "Mouse-Up Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}