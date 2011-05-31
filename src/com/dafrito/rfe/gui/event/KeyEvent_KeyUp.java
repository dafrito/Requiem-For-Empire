/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class KeyEvent_KeyUp extends RiffInterface_KeyEvent implements Nodeable {
	public KeyEvent_KeyUp(int key) {
		super(key);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Key Events", "Key-Up Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}