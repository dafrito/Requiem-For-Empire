/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class RiffInterface_KeyEvent implements Nodeable, RiffInterface_Event {
	private int key;

	public RiffInterface_KeyEvent(int key) {
		this.key = key;
	}

	public int getKeyCode() {
		return this.key;
	}

	@Override
	public void nodificate() {
		assert Debugger.addNode("Key code: " + this.key);
	}
}