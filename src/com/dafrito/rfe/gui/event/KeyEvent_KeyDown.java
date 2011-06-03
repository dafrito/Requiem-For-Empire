/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class KeyEvent_KeyDown extends RiffInterface_KeyEvent implements Nodeable {
	public KeyEvent_KeyDown(int key) {
		super(key);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Key Events", "Key-Down Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}