package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

class KeyEvent_KeyDown extends RiffInterface_KeyEvent implements Nodeable {
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

class KeyEvent_KeyUp extends RiffInterface_KeyEvent implements Nodeable {
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

class RiffInterface_KeyEvent implements Nodeable, RiffInterface_Event {
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

public interface RiffInterface_KeyListener {
	public void riffKeyEvent(RiffInterface_KeyEvent event);
}
