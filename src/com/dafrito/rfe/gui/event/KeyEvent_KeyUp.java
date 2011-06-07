/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class KeyEvent_KeyUp extends RiffInterface_KeyEvent {
	public KeyEvent_KeyUp(int key) {
		super(key);
	}
}